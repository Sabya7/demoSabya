package org.valtech.marutbackendapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valtech.marutbackendapp.dao.CustomerDao;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerDao,String> {

//    @Query("select c.customerId from CustomerDao c where c.shoppingListId = ?1")
    CustomerDao findByShoppingListId(String lineItemId);

}
