package com.leonp967.sweexpress.desafioJava.factory;

import com.leonp967.sweexpress.desafioJava.model.ProcessorIdEnum;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.DataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.SalesDataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.SalesmanDataProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class ProcessorFactory {

    @Value("${item.delimiter}")
    private String ITEM_DELIMITER;

    @Value("${item.attributes.delimiter}")
    private String ITEM_ATTRIBUTES_DELIMITER;

    @Bean
    public DataProcessor customerProcessor() {
        return new CustomerDataProcessor();
    }

    @Bean
    public DataProcessor salesProcessor() {
        return new SalesDataProcessor(ITEM_ATTRIBUTES_DELIMITER, ITEM_DELIMITER);
    }

    @Bean
    public DataProcessor salesmanProcessor() {
        return new SalesmanDataProcessor();
    }

    public DataProcessor getProcessor(String processorId) {
        ProcessorIdEnum processorIdEnum = ProcessorIdEnum.fromId(processorId);
        switch (processorIdEnum) {
            case SALESMAN_PROCESSOR:
                return salesmanProcessor();
            case CUSTOMER_PROCESSOR:
                return customerProcessor();
            case SALES_PROCESSOR:
                return salesProcessor();
            default:
                throw new IllegalArgumentException("No processor available with that id!");
        }
    }
}
