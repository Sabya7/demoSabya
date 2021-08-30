package org.valtech.marutbackendapp.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.valtech.marutbackendapp.csvconverters.CsvProductKeyReferenceConverter;
import org.valtech.marutbackendapp.csvconverters.CsvProductVariantKeyReferenceConverter;
import org.valtech.marutbackendapp.csvconverters.CsvTypedMoneyConverter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceImportModel {

    @CsvBindByName
    private String key;

    @CsvCustomBindByName(column = "value", converter =  CsvTypedMoneyConverter.class)
    private com.commercetools.importapi.models.common.TypedMoney value;

    @CsvCustomBindByName(column = "productVariant", converter =  CsvProductVariantKeyReferenceConverter.class)
    private com.commercetools.importapi.models.common.ProductVariantKeyReference productVariant;

    @CsvCustomBindByName(column = "product",converter = CsvProductKeyReferenceConverter.class)
    private com.commercetools.importapi.models.common.ProductKeyReference product;
}
