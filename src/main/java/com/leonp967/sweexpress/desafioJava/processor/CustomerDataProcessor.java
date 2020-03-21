package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.FileDataModel;

import java.util.List;

public class CustomerDataProcessor implements DataProcessor {

    @Override
    public FileDataModel process(List<String> attributes) {
        long cnpj = Long.parseLong(attributes.get(0));
        String name = attributes.get(1);
        String businessArea = attributes.get(2);

        return Customer.builder()
                .businessArea(businessArea)
                .cnpj(cnpj)
                .name(name)
                .build();
    }
}
