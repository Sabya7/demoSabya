package org.valtech.marutbackendapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valtech.marutbackendapp.dao.ShoppingListDao;
import org.valtech.marutbackendapp.dao.ShoppingListPrimaryKey;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListDao, ShoppingListPrimaryKey> {

    @Query("select DISTINCT s.shoppingListId from ShoppingListDao s where s.lineItemId = ?1")
    List<String> findByLineItemId(String lineItemId);
}
