package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Salesman;

import java.util.List;

public class SalesmanDataProcessor implements DataProcessor {

    @Override
    public FileDataModel process(List<String> attributes) {
        long cpf = Long.parseLong(attributes.get(0));
        String name = attributes.get(1);
        double salary = Double.parseDouble(attributes.get(2));

        return Salesman.builder()
                .cpf(cpf)
                .name(name)
                .salary(salary)
                .build();
    }
}
