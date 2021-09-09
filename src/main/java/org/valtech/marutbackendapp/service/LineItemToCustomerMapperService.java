package org.valtech.marutbackendapp.service;

import com.azure.core.util.BinaryData;
import com.commercetools.api.models.message.ProductPriceDiscountsSetUpdatedPrice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.valtech.marutbackendapp.dao.LineItemDao;
import org.valtech.marutbackendapp.repository.LineItemRepository;
import org.valtech.marutbackendapp.repository.ShoppingListRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class LineItemToCustomerMapperService {

    @Autowired
    ShoppingListRepository shoppingListRepository;

    @Autowired
    LineItemRepository lineItemRepository;

    public HashMap<String, List<String>> map(List<BinaryData> body) throws JsonProcessingException, ExecutionException, InterruptedException {

        HashMap<String, List<String>> shoppingListToListOfLineItemsMap
                = new HashMap<>();
        for (BinaryData data : body) {

            String nodeString = data.toObject(JsonNode.class).get("updatedPrices").toString();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JSR310Module());
            ProductPriceDiscountsSetUpdatedPrice updatedPrice = mapper.readValue(nodeString, ProductPriceDiscountsSetUpdatedPrice.class);
            String sku = updatedPrice.getSku();

            LineItemDao lineItemDao = lineItemRepository.findBySku(sku);
            List<String> shoppingListIds
                    = shoppingListRepository.findByLineItemId(lineItemDao.getLineItemId());

            for (String shoppingListId : shoppingListIds) {
                List<String> list =
                        shoppingListToListOfLineItemsMap.getOrDefault(shoppingListId, new ArrayList<>());
                list.add(lineItemDao.getLineItemId());
                shoppingListToListOfLineItemsMap.put(shoppingListId, list);
            }

        }

        return shoppingListToListOfLineItemsMap;
    }
}
