package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.SaleItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SalesDataProcessor implements DataProcessor {

    private String ITEM_ATTRIBUTES_DELIMITER;
    private String ITEM_DELIMITER;

    public SalesDataProcessor(String itemAttributesDelimiter, String itemDelimiter){
        ITEM_ATTRIBUTES_DELIMITER = itemAttributesDelimiter;
        ITEM_DELIMITER = itemDelimiter;
    }

    @Override
    public FileDataModel process(List<String> attributes) {
        int id = Integer.parseInt(attributes.get(0));
        String salesmanName = attributes.get(2);

        String saleListString = attributes.get(1);
        saleListString = saleListString.substring(1, saleListString.length()-1);
        String[] saleItemArray = saleListString.split(ITEM_DELIMITER);
        List<SaleItem> saleItemList = Arrays.stream(saleItemArray)
                .map(item -> {
                    String[] itemAttributes = item.split(ITEM_ATTRIBUTES_DELIMITER);
                    int itemId = Integer.parseInt(itemAttributes[0]);
                    int quantity = Integer.parseInt(itemAttributes[1]);
                    double price = Double.parseDouble(itemAttributes[2]);
                    return SaleItem.builder()
                            .id(itemId)
                            .quantity(quantity)
                            .price(price)
                            .build();
                }).collect(Collectors.toList());

        return Sale.builder()
                .id(id)
                .itemsList(saleItemList)
                .salesmanName(salesmanName)
                .build();
    }
}
