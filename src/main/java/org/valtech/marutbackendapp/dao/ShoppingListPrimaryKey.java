package org.valtech.marutbackendapp.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListPrimaryKey implements Serializable
{

    private String shoppingListId;

    private String lineItemId;
}

