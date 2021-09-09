package org.valtech.marutbackendapp.service;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.shopping_list.ShoppingList;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.valtech.marutbackendapp.dao.CustomerDao;
import org.valtech.marutbackendapp.repository.CustomerRepository;

import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerService {

    //http client to connect to commercetools API
    @Autowired
    private ApiRoot ctoolsHttpApiClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShoppingListService shoppingListService;

    //value for project name
    @Value("${ct.project}")
    private String project;

    @Transactional
    @Async
    public CompletableFuture<CustomerDao> createCustomer(CustomerDraft customerDraft, String pnsHandle) {

        return
                ctoolsHttpApiClient.withProjectKey(project)
                        .customers()
                        .post(customerDraft)
                        .execute()
                        .thenApply(response -> response.getBody().getCustomer())
                        .thenCompose(shoppingListService :: createShoppingList)
                        .thenApply(response -> response.getBody().getCustomer().getObj())
                        .thenApply(this :: createDaoFromCustomerAndSave)
                        .thenApply(customerDao ->
                        {
                            customerDao.setFcmToken(pnsHandle);
                            return customerRepository.save(customerDao);
                        });



    }

    @Transactional
    public CompletableFuture<CustomerDao> updateFirstName(String id, String firstName) {

        CustomerSetFirstNameAction custAction =
                CustomerSetFirstNameActionBuilder.of().firstName(firstName).build();

        CustomerUpdateAction customerUpdateAction = CustomerSetFirstNameAction.of()
                .withCustomerSetFirstNameAction(action -> custAction);

        return updateCustomer(customerUpdateAction, id)
                .thenApply(ApiHttpResponse::getBody)
                .thenApply(this::createDaoFromCustomerAndSave);
    }

    @Transactional
    public CompletableFuture<CustomerDao> updateLastName(String id, String lastName) {
        CustomerSetLastNameAction custAction =
                CustomerSetLastNameActionBuilder.of().lastName(lastName).build();

        CustomerUpdateAction customerUpdateAction = CustomerSetLastNameAction.of()
                .withCustomerSetLastNameAction(action -> custAction);

        return updateCustomer(customerUpdateAction, id)
                .thenApply(ApiHttpResponse::getBody)
                .thenApply(this::createDaoFromCustomerAndSave);
    }

    public CompletableFuture<ApiHttpResponse<Customer>> addAddress(String id, Address address) {
        CustomerAddAddressAction customerAddAddressAction =
                CustomerAddAddressActionBuilder.of().address(address).build();

        CustomerUpdateAction customerUpdateAction = CustomerAddAddressAction.of()
                .withCustomerAddAddressAction(action -> customerAddAddressAction);

        return updateCustomer(customerUpdateAction,id);
    }

    public CompletableFuture<ApiHttpResponse<Customer>> changeAddress(String id, String addressId, Address address) {
        CustomerChangeAddressAction customerChangeAddressAction =
                CustomerChangeAddressActionBuilder.of().
                        addressId(addressId).address(address).build();

        CustomerUpdateAction customerUpdateAction = CustomerChangeAddressAction.of()
                .withCustomerChangeAddressAction(action -> customerChangeAddressAction);

        return updateCustomer(customerUpdateAction,id);

    }

    public CompletableFuture<ApiHttpResponse<Customer>> removeAddress(String id, String addressId) {
        CustomerRemoveAddressAction customerRemoveAddressAction =
                CustomerRemoveAddressActionBuilder.of().
                        addressId(addressId).build();

        CustomerUpdateAction customerUpdateAction = CustomerRemoveAddressAction.of()
                .withCustomerRemoveAddressAction(action -> customerRemoveAddressAction);

        return updateCustomer(customerUpdateAction,id);
    }

    private CompletableFuture<ApiHttpResponse<Customer>> updateCustomer
            (CustomerUpdateAction customerUpdateAction, String id) {
        return retrieveCustomer(id)
                .thenCompose(
                        customerApiHttpResponse ->
                                ctoolsHttpApiClient.withProjectKey(project).customers().withId(id)
                                        .post(
                                                CustomerUpdateBuilder.of()
                                                        .actions(customerUpdateAction)
                                                        .version(customerApiHttpResponse
                                                                .getBody()
                                                                .getVersion()
                                                        )
                                                        .build()
                                        )
                                        .execute()
                );
    }


    public CompletableFuture<ApiHttpResponse<Customer>> retrieveCustomer(String id) {
        return
                ctoolsHttpApiClient.withProjectKey(project).
                        customers()
                        .withId(id)
                        .get()
                        .execute();
    }

    public CustomerDao createDaoFromCustomerAndSave(Customer customer) {

        CustomerDao customerDao = new CustomerDao(
                customer.getId(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getDefaultShippingAddressId()
        );

        return customerRepository.save(customerDao);
//        return CompletableFuture.supplyAsync(() -> customerDao);

    }


    public CompletableFuture<ApiHttpResponse<Customer>> setDefaultShippingAddress(String id, String addressId) {

        CustomerSetDefaultBillingAddressAction setDefaultBillingAddressAction =
                CustomerSetDefaultBillingAddressActionBuilder.of()
                        .addressId(addressId)
                        .build();

        CustomerUpdateAction customerUpdateAction = CustomerSetDefaultBillingAddressAction.of()
                .withCustomerSetDefaultBillingAddressAction(action -> setDefaultBillingAddressAction);

        return updateCustomer(customerUpdateAction,id);
    }

}
