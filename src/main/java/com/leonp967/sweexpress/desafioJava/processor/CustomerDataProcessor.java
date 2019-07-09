package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class CustomerDataProcessor implements DataProcessor {

    private ApplicationContext applicationContext;

    public CustomerDataProcessor(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public FileDataModel process(List<String> attributes) {
        long cnpj = Long.parseLong(attributes.get(0));
        String name = attributes.get(1);
        String businessArea = attributes.get(2);
        return (FileDataModel) applicationContext.getBean("customer", cnpj, name, businessArea);
    }
}
