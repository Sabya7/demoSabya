package org.valtech.marutbackendapp.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "LINE_ITEM")
@AllArgsConstructor
@NoArgsConstructor
public class LineItemDao {

    @Id
    private String lineItemId;

    private String sku;

}
