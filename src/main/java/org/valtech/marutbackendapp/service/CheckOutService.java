package org.valtech.marutbackendapp.service;

import com.azure.core.util.BinaryData;
import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressImpl;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.message.ProductPriceDiscountsSetUpdatedPrice;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderFromCartDraft;
import com.commercetools.api.models.order.OrderFromCartDraftBuilder;
import com.commercetools.api.models.shopping_list.ShoppingListLineItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.windowsazure.messaging.NotificationHubsException;
import com.windowsazure.messaging.NotificationOutcome;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.valtech.marutbackendapp.dao.CustomerDao;
import org.valtech.marutbackendapp.dao.LineItemDao;
import org.valtech.marutbackendapp.repository.CustomerRepository;
import org.valtech.marutbackendapp.repository.LineItemRepository;
import org.valtech.marutbackendapp.repository.ShoppingListRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CheckOutService {

    @Value("${ct.project}")
    private String project;

    @Autowired
    private ApiRoot ctoolsHttpApiClient;

    @Autowired
    LineItemRepository lineItemRepository;

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ShoppingListService shoppingListService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Async
    public void lineItemsToCustomerMapper(List<BinaryData> body) throws JsonProcessingException, ExecutionException, InterruptedException {

        HashMap<String,List<String>> shoppingListToListOfLineItemsMap
                            = new HashMap<>();
        for(BinaryData data : body)
        {

            String nodeString = data.toObject(JsonNode.class).get("updatedPrices").toString();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JSR310Module());
            ProductPriceDiscountsSetUpdatedPrice updatedPrice = mapper.readValue(nodeString, ProductPriceDiscountsSetUpdatedPrice.class);
            String sku = updatedPrice.getSku();

            LineItemDao lineItemDao = lineItemRepository.findBySku(sku);
            List<String> shoppingListIds
                    = shoppingListRepository.findByLineItemId(lineItemDao.getLineItemId());

           for (String shoppingListId : shoppingListIds) {
               List<String> list =
                       shoppingListToListOfLineItemsMap.getOrDefault(shoppingListId,new ArrayList<>());
               list.add(lineItemDao.getLineItemId());
               shoppingListToListOfLineItemsMap.put(shoppingListId, list);
           }

        }

        List<NotificationOutcome> notificationOutcomes
                = createCartFromShoppingListLineItem(shoppingListToListOfLineItemsMap);
        System.out.println(notificationOutcomes);
    }

    private List<NotificationOutcome> createCartFromShoppingListLineItem(HashMap<String, List<String>> shoppingListToListOfLineItemsMap) throws ExecutionException, InterruptedException {

       List<NotificationOutcome> notificationOutcomes = new ArrayList<>();

       for (Map.Entry<String, List<String>> entry : shoppingListToListOfLineItemsMap.entrySet())
       {
          CustomerDao customerDao = customerRepository.findByShoppingListId(entry.getKey());

          cartFromDiscountedItems(entry.getKey(),customerDao.getId(),entry.getValue())
                  .thenCompose(cartResponse ->
                  {
                      try {
                          notificationOutcomes.add( pushNotificationService
                                  .sendPushNotification(customerDao.getFcmToken(),cartResponse));
                      } catch (NotificationHubsException e) {
                          e.printStackTrace();
                      }
                      return null;
                  });
       }

       return notificationOutcomes;

    }

    public CompletableFuture<ApiHttpResponse<Cart>> cartFromDiscountedItems(String shoppingListId, String customerId, List<String> lineItemIds) throws ExecutionException, InterruptedException {

        //Step - 1: Create Array of LineItems from array of LineItem IDs

        //fetch all the lineItems in shopping list and filter the discounted lineItems.
        List<ShoppingListLineItem> listLineItems =
                ctoolsHttpApiClient.withProjectKey(project)
                        .shoppingLists()
                        .withId(shoppingListId)
                        .get()
                        .execute()
                        .get().getBody().getLineItems()
                        .stream()
                        .filter(listLineItem -> lineItemIds.contains(listLineItem.getId()))
                        .collect(Collectors.toList());

        ArrayList<LineItemDraft> lineItemDrafts = new ArrayList<>();

        for(ShoppingListLineItem listLineItem :listLineItems )
        {
            lineItemDrafts.add(
                    LineItemDraftBuilder.of()
                            .variantId(listLineItem.getVariantId())
                            .quantity((long)listLineItem.getQuantity())
                            .build()
            );
            //Remove this listLineItem from shoppingList as well.
            shoppingListService.removeLineItem(shoppingListId,
                    listLineItem.getId(), Long.valueOf(listLineItem.getQuantity()));
        }

        //Step-2 : Create a cart.
        /*
         * Here, currency is hard-coded, but logic needs to be added
         * so that it can be determined from customer's country.
         * */

        Customer customer =
                customerService.retrieveCustomer(customerId)
                        .get()
                        .getBody();


        Optional<Address> shippingAddress = customer.getAddresses().stream()
                .filter( address -> address.getId().equals(customer.getDefaultShippingAddressId()))
                .findFirst();

        CartDraft cartDraft = CartDraftBuilder.of()
                .currency("USD")
                .customerId(customerId)
                .lineItems(lineItemDrafts)
                .shippingAddress(shippingAddress.orElseGet(AddressImpl::new)) // will refactor this
                .build();


        return ctoolsHttpApiClient
                .withProjectKey(project)
                .carts()
                .post(cartDraft)
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> checkOut(String cartId) throws ExecutionException, InterruptedException {
        OrderFromCartDraft orderFromCartDraft = OrderFromCartDraftBuilder.of()
                .cart(
                        CartResourceIdentifierBuilder.of()
                                .id(cartId)
                                .build()
                ).version(
                        ctoolsHttpApiClient.withProjectKey(project).carts()
                                .withId(cartId)
                                .get().execute()
                                .get().getBody().getVersion()
                ).build();

        return ctoolsHttpApiClient.withProjectKey(project).orders()
                .post(orderFromCartDraft)
                .execute();
    }
}
