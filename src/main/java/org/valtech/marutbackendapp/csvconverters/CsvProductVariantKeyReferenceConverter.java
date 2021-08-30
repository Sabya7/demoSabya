package org.valtech.marutbackendapp.csvconverters;

import com.commercetools.importapi.models.common.ProductVariantKeyReference;
import com.commercetools.importapi.models.common.ProductVariantKeyReferenceBuilder;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvProductVariantKeyReferenceConverter extends AbstractBeanField<ProductVariantKeyReference, String> {

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return ProductVariantKeyReferenceBuilder.of()
                .key(value).build();
    }
}
