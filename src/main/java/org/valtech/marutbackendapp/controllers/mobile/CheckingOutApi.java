package org.valtech.marutbackendapp.controllers.mobile;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressImpl;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderFromCartDraft;
import com.commercetools.api.models.order.OrderFromCartDraftBuilder;
import com.commercetools.api.models.shopping_list.ShoppingListLineItem;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.valtech.marutbackendapp.service.CheckOutService;
import org.valtech.marutbackendapp.service.CustomerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class CheckingOutApi {


    @Autowired
    CheckOutService checkOutService;

    @PostMapping("/cartFromDiscountedItems")
    public CompletableFuture<ApiHttpResponse<Cart>> cartFromDiscountedItems(String shoppingListId, String customerId,
                                                                            List<String> lineItemIds) throws ExecutionException, InterruptedException {
      return checkOutService.cartFromDiscountedItems(shoppingListId,customerId,lineItemIds);
    }

    @PostMapping("/checkOut")
    public CompletableFuture<ApiHttpResponse<Order>> checkOut(String cartId) throws ExecutionException, InterruptedException {

        return checkOutService.checkOut(cartId);
    }

}
