package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class SalesmanDataProcessor implements DataProcessor {

    private ApplicationContext applicationContext;

    public SalesmanDataProcessor(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public FileDataModel process(List<String> attributes) {
        long cpf = Long.parseLong(attributes.get(0));
        String name = attributes.get(1);
        double salary = Double.parseDouble(attributes.get(2));
        return (FileDataModel) applicationContext.getBean("salesman", cpf, name, salary);
    }
}
