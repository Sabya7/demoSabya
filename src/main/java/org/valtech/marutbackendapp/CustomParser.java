package org.valtech.marutbackendapp;

import com.commercetools.api.models.product.Attribute;
import com.commercetools.api.models.product.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CustomParser {

   static String custJsonString = "{\"id\":\"aeea0a34-4bdb-4242-93a4-025b897dec32\",\"version\":8,\"lastMessageSequenceNumber\":1," +
            "\"createdAt\":\"2021-09-07T06:48:35.315Z\",\"lastModifiedAt\":\"2021-09-07T08:21:29.671Z\",\"lastModifiedBy\":" +
            "{\"isPlatformClient\":true,\"user\":{\"typeId\":\"user\",\"id\":\"2b9e031e-8efb-457a-bde4-a949bcf54fb8\"}}" +
            ",\"createdBy\":{\"isPlatformClient\":true,\"user\":{\"typeId\":\"user\",\"id\":\"2b9e031e-8efb-457a-bde4-a949bcf54fb8\"}}" +
            ",\"productType\":{\"typeId\":\"product-type\",\"id\":\"026a61a9-44db-4be2-8c3f-a45e02253f40\"},\"masterData\":{\"current\"" +
            ":{\"name\":{\"en\":\"Nike-Sneakers\"},\"categories\":[],\"categoryOrderHints\":{},\"slug\":{\"en\":\"nike-sneakers\"},\"metaTitle\"" +
            ":{\"en\":\"\",\"de\":\"\"},\"metaDescription\":{\"en\":\"\",\"de\":\"\"},\"masterVariant\":{\"id\":1,\"sku\":\"NIKE01\",\"key\":\"var-key-01\"," +
            "\"prices\":[],\"images\":[],\"attributes\":[{\"name\":\"sizes\",\"value\":6},{\"name\":\"colors\",\"value\":\"black\"}]," +
            "\"assets\":[]},\"variants\":[],\"searchKeywords\":{}},\"staged\":{\"name\":{\"en\":\"Nike-Sneakers\"},\"categories\":[]" +
            ",\"categoryOrderHints\":{},\"slug\":{\"en\":\"nike-sneakers\"},\"metaTitle\":{\"en\":\"\",\"de\":\"\"},\"masterVariant\":{\"id\":1,\"sku\":\"NIKE01\"" +
            ",\"key\":\"var-key-01\",\"prices\":[],\"images\":[],\"attributes\":[{\"name\":\"sizes\",\"value\":11},{\"name\":\"colors\",\"value\":\"black\"}],\"assets\":[]},\"variants\":[],\"searchKeywords\":{}}," +
            "\"published\":false,\"hasStagedChanges\":true},\"key\":\"Nike-Sneakers\",\"taxCategory\":{\"typeId\":\"tax-category\",\"id\":\"30c4d69f-75e0-49f2-bb0a-69144acea94e\"},\"lastVariantId\":1}";



       public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
//        mapper.registerModule(new BlackbirdModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        long start = System.currentTimeMillis();
        Product product = mapper.readValue(custJsonString,Product.class);

//           Product pro = JsonIterator.deserialize(custJsonString, ProductImpl.class);
        long end = System.currentTimeMillis();
           System.out.println(end-start);
//        System.out.println(ToStringBuilder.reflectionToString(product, new MultilineRecursiveToStringStyle()));

    }
}
