package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.factory.ProcessorFactory;
import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.DataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.SalesmanDataProcessor;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import java.util.List;

public class DataProcessingCommand extends HystrixObservableCommand<FileDataModel> {

    private List<String> attributes;
    private DataProcessor dataProcessor;
    private Logger LOGGER = LoggerFactory.getLogger(DataProcessingCommand.class);

    @Autowired
    private ProcessorFactory processorFactory;

    public DataProcessingCommand(String commandGroup, List<String> attributes) {
        super(HystrixCommandGroupKey.Factory.asKey(commandGroup));
        this.attributes = attributes;
    }

    @Override
    protected Observable<FileDataModel> construct() {
        dataProcessor = processorFactory.getProcessor(attributes.get(0));
        LOGGER.info("Processing data with ID: {}", attributes.get(0));
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(dataProcessor.process(attributes.subList(1, attributes.size())));
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    protected Observable<FileDataModel> resumeWithFallback() {
        LOGGER.error("Error while processing the data: {}", getExecutionException().getStackTrace(), getExecutionException());
        return Observable.create(subscriber -> {
            FileDataModel dataModel;
            if (dataProcessor instanceof CustomerDataProcessor) {
                dataModel = Customer.builder().build();
            } else if (dataProcessor instanceof SalesmanDataProcessor) {
                dataModel = Salesman.builder().build();
            } else {
                dataModel = Sale.builder().build();
            }
            subscriber.onNext(dataModel);
            subscriber.onCompleted();
        });
    }
}