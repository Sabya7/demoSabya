package org.valtech.marutbackendapp.csvconverters;


import com.commercetools.importapi.models.common.MoneyBuilder;
import com.commercetools.importapi.models.common.TypedMoney;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvTypedMoneyConverter extends AbstractBeanField<TypedMoney,String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        String[] money = value.split(";");
        return
                MoneyBuilder.of()
                        .centAmount(Long.valueOf(money[1]))
                        .currencyCode(money[0])
                        .build();

    }
}
