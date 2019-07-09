package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import com.leonp967.sweexpress.desafioJava.processor.DataProcessor;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DataProcessingCommand extends HystrixCommand<CompletableFuture<FileDataModel>> {

    private List<String> attributes;
    private DataProcessor dataProcessor;
    private Logger logger = LoggerFactory.getLogger(DataProcessingCommand.class);
    private ExecutorService executorService;

    public DataProcessingCommand(String commandGroup, ApplicationContext applicationContext, List<String> attributes, ExecutorService executorService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup)).andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(300)));
        this.attributes = attributes;
        this.dataProcessor = (DataProcessor) applicationContext.getBean("dataProcessor" + attributes.get(0));
        this.executorService = executorService;
    }

    @Override
    protected CompletableFuture<FileDataModel> run() {
        return CompletableFuture.supplyAsync(() -> dataProcessor.process(attributes.subList(1, attributes.size())), executorService)
                .exceptionally(throwable -> {
                    try {
                        return getFallback().get();
                    } catch (Exception e) {
                        return new Salesman(0L, "", 0);
                    }
                });
    }

    @Override
    protected CompletableFuture<FileDataModel> getFallback() {
        logger.error("Error on DataProcessingCommand", getExecutionException());
        return CompletableFuture.completedFuture(new Salesman(0L, "", 0));
    }

    public List<String> getAttributes() {
        return attributes;
    }
}