package org.valtech.marutbackendapp.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "SHOPPING_LIST")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ShoppingListPrimaryKey.class)
public class ShoppingListDao {

    @Id
    private String shoppingListId;

    @Id
    private String lineItemId;

    private long quantity;
}
