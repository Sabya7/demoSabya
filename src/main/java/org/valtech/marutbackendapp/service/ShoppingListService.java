package org.valtech.marutbackendapp.service;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerResourceIdentifierBuilder;
import com.commercetools.api.models.shopping_list.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valtech.marutbackendapp.dao.LineItemDao;
import org.valtech.marutbackendapp.dao.ShoppingListDao;
import org.valtech.marutbackendapp.dao.ShoppingListPrimaryKey;
import org.valtech.marutbackendapp.repository.LineItemRepository;
import org.valtech.marutbackendapp.repository.ShoppingListRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ShoppingListService {

    @Autowired
    private ApiRoot ctoolsHttpApiClient;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    //value for project name
    @Value("${ct.project}")
    private String project;


    public CompletableFuture<ApiHttpResponse<ShoppingList>> createShoppingList(Customer customer) {

        ShoppingListDraft shoppingListDraft = ShoppingListDraftBuilder
                .of()
                .name(
                        LocalizedStringBuilder
                                .of()
                                .addValue("en",customer.getFirstName()+"'s list")
                                .build()
                )
                .customer(
                        CustomerResourceIdentifierBuilder.of()
                                .id(customer.getId())
                                .build()
                )
                .build();

        return
                ctoolsHttpApiClient.withProjectKey(project)
                        .shoppingLists()
                        .post(shoppingListDraft)
                        .execute();

    }

    public CompletableFuture<ApiHttpResponse<ShoppingList>> setCustomer(String id, String customerId) {

        ShoppingListSetCustomerAction shoppingListSetCustomerAction = ShoppingListSetCustomerActionBuilder
                .of()
                .customer(
                        CustomerResourceIdentifierBuilder.of()
                                .id(customerId)
                                .build()
                )
                .build();

        ShoppingListUpdateAction shoppingListUpdateAction =
                ShoppingListSetCustomerAction.of()
                        .withShoppingListSetCustomerAction(action -> shoppingListSetCustomerAction);

        return updateShoppingList(shoppingListUpdateAction,id);

    }

    @Transactional
    public CompletableFuture<ShoppingListDao> addLineItem(String id, String SKU, Optional<Long> quantity) {

        ShoppingListAddLineItemAction listAddLineItemAction = ShoppingListAddLineItemActionBuilder.of()
                .sku(SKU)
                .quantity(quantity.orElse(1L))
                .build();

        ShoppingListUpdateAction shoppingListUpdateAction =
                ShoppingListAddLineItemAction.of()
                        .withShoppingListAddLineItemAction(action -> listAddLineItemAction);

        return updateShoppingList(shoppingListUpdateAction,id)
                .thenApply(ApiHttpResponse::getBody)
                .thenApply(this::addLineItemToDB)
                .thenApply(this::createDaoFromShoppingListAndSave);
    }

    private ShoppingList addLineItemToDB(ShoppingList shoppingList) {

        int index = shoppingList.getLineItems().size()-1;
        LineItemDao lineItemDao = new LineItemDao(
                shoppingList.getLineItems().get(index).getId(),
                shoppingList.getLineItems().get(index).getVariant().getSku()
        );
        lineItemRepository.save(lineItemDao);

        return shoppingList;

    }

    public CompletableFuture<ShoppingListDao> changeLineItemQuantity(String id, String lineItemId, Long quantity) {
        ShoppingListChangeLineItemQuantityAction changeLineItemQuantityAction =
                ShoppingListChangeLineItemQuantityActionBuilder.of()
                        .quantity(quantity)
                        .lineItemId(lineItemId)
                        .build();
        ShoppingListUpdateAction shoppingListUpdateAction = ShoppingListChangeLineItemQuantityAction
                .of().withShoppingListChangeLineItemQuantityAction(action -> changeLineItemQuantityAction);

        return updateShoppingList(shoppingListUpdateAction,id)
                .thenApply(ApiHttpResponse::getBody)
                .thenApply(this::createDaoFromShoppingListAndSave);
    }

    public CompletableFuture<Void> removeLineItem(String id, String lineItemId, Long quantity) {

        ShoppingListRemoveLineItemAction shoppingListRemoveLineItemAction =
                ShoppingListRemoveLineItemActionBuilder.of()
                        .quantity(quantity)
                        .lineItemId(lineItemId)
                        .build();
        ShoppingListUpdateAction shoppingListUpdateAction = ShoppingListRemoveLineItemAction
                .of().withShoppingListRemoveLineItemAction(action -> shoppingListRemoveLineItemAction);

        return updateShoppingList(shoppingListUpdateAction,id)
                .thenApply(ApiHttpResponse::getBody)
                .thenAccept(shoppingList -> shoppingListRepository
                        .deleteById(new ShoppingListPrimaryKey(id,lineItemId)));
    }

    private CompletableFuture<ApiHttpResponse<ShoppingList>> updateShoppingList
            (ShoppingListUpdateAction shoppingListUpdateAction, String id)
    {
        return retrieveShoppingList(id)
                .thenCompose(
                        shoppingListApiHttpResponse ->
                                ctoolsHttpApiClient.withProjectKey(project).shoppingLists().withId(id)
                                        .post(
                                                ShoppingListUpdateBuilder.of()
                                                        .actions(shoppingListUpdateAction)
                                                        .version(shoppingListApiHttpResponse
                                                                .getBody()
                                                                .getVersion()
                                                        )
                                                        .build()
                                        )
                                        .execute()
                );
    }

    private CompletableFuture<ApiHttpResponse<ShoppingList>> retrieveShoppingList(String id)
    {
        return
                ctoolsHttpApiClient.withProjectKey(project)
                        .shoppingLists()
                        .withId(id)
                        .get()
                        .execute();
    }

    private ShoppingListDao createDaoFromShoppingListAndSave(ShoppingList shoppingList) {

        int index = shoppingList.getLineItems().size() - 1;

        ShoppingListDao shoppingListDao =  new ShoppingListDao(
                    shoppingList.getId(),
                    shoppingList.getLineItems().get(index).getId(),
                    shoppingList.getLineItems().get(index).getQuantity() );

        return shoppingListRepository.save(shoppingListDao);
    }

}
