package org.valtech.marutbackendapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valtech.marutbackendapp.dao.LineItemDao;

@Repository
public interface LineItemRepository extends JpaRepository<LineItemDao,String> {

         LineItemDao findBySku(String sku);
}
