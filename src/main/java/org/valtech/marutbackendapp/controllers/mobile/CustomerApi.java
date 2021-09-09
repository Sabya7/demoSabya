package org.valtech.marutbackendapp.controllers.mobile;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.valtech.marutbackendapp.dao.CustomerDao;
import org.valtech.marutbackendapp.service.CustomerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
* The Mobile App's customer profile should consume this controller.
* Usages:
*       Customer can create his identity.
*       Customer can update certain fields.
*       Customer can delete itself.(optional functionality)
*
* This controller is like a bridge between app and CT.
* */

@RestController
public class CustomerApi {


    @Autowired
    private CustomerService customerService;

    //signing up the customer
    @PostMapping("/createACustomer")
    public CompletableFuture<CustomerDao> createACustomer(@RequestBody CustomerDraft customerDraft
            ,@RequestParam String pnsHandle) {

       return customerService.createCustomer(customerDraft,pnsHandle);

    }

    @PostMapping("/updateFirstName")
    public CompletableFuture<CustomerDao> updateFirstName(String id, String firstName) throws ExecutionException, InterruptedException {
        return customerService.updateFirstName(id,firstName);
    }

    @PostMapping("/updateLastName")
    public CompletableFuture<CustomerDao> updateLastName(String id, String lastName) throws ExecutionException, InterruptedException {
        return customerService.updateLastName(id,lastName);
    }

    @PostMapping("/addAddress")
    public CompletableFuture<ApiHttpResponse<Customer>> addAddress(String id, Address address) throws ExecutionException, InterruptedException {
        return customerService.addAddress(id,address);
    }

    @PostMapping("/changeAddress")
    public CompletableFuture<ApiHttpResponse<Customer>> changeAddress(String id, String addressId, Address address) throws ExecutionException, InterruptedException {
        return customerService.changeAddress(id,addressId,address);
    }

    @PostMapping("/removeAddress")
    public CompletableFuture<ApiHttpResponse<Customer>> removeAddress(String id, String addressId) throws ExecutionException, InterruptedException {
        return customerService.removeAddress(id,addressId);
    }

    @PostMapping("/setDefaultShippingAddress")
    public CompletableFuture<ApiHttpResponse<Customer>> setShippingAddress(String id, String addressId) throws ExecutionException, InterruptedException {

        return customerService.setDefaultShippingAddress(id,addressId);

    }

}
