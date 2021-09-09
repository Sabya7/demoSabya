package org.valtech.marutbackendapp.controllers.mobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.valtech.marutbackendapp.dao.ShoppingListDao;
import org.valtech.marutbackendapp.service.ShoppingListService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class ShoppingListApi {



    @Autowired
    private ShoppingListService shoppingListService;

//    @PostMapping("/createShoppingList")
//    public CompletableFuture<ApiHttpResponse<ShoppingList>> createShoppingList(ShoppingListDraft shoppingListDraft) throws ExecutionException, InterruptedException {
//        return shoppingListService.createShoppingList(shoppingListDraft);
//    }

//    @PostMapping("/setCustomer")
//    public CompletableFuture<ApiHttpResponse<ShoppingList>> setCustomer(String id, String customerId) throws ExecutionException, InterruptedException {
//       return shoppingListService.setCustomer(id,customerId);
//    }

    @PostMapping("/addLineItem")
    public CompletableFuture<ShoppingListDao> addLineItem(String id, String SKU, Optional<Long> quantity)
            throws ExecutionException, InterruptedException {
        return shoppingListService.addLineItem(id,SKU,quantity);
    }

    @PostMapping("/changeLineItemQuantity")
    public CompletableFuture<ShoppingListDao> changeLineItemQuantity
            (String id,String lineItemId, Long quantity) throws ExecutionException, InterruptedException {
        return shoppingListService.changeLineItemQuantity(id,lineItemId,quantity);
    }

    @PostMapping("/removeLineItem")
    public CompletableFuture<Void> removeLineItem
            (String id,String lineItemId, Long quantity) throws ExecutionException, InterruptedException {
        return shoppingListService.removeLineItem(id,lineItemId,quantity);
    }


}
