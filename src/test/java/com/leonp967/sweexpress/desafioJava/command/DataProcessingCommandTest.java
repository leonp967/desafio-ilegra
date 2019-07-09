package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataProcessingCommandTest {

    private DataProcessingCommand command;
    private ApplicationContext applicationContext;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp(){
        executorService = Executors.newFixedThreadPool(5);
        applicationContext = mock(ApplicationContext.class);
    }

    @Test
    public void testSuccessfulProcessing() throws ExecutionException, InterruptedException {
        CustomerDataProcessor customerDataProcessor = mock(CustomerDataProcessor.class);
        when(customerDataProcessor.process(any()))
                .thenReturn(new Customer(123456789, "John", "Vehicles"));
        when(applicationContext.getBean(anyString()))
                .thenReturn(customerDataProcessor);
        command = new DataProcessingCommand("", applicationContext, Arrays.asList("002", "123456789", "John", "Vehicles"), executorService);

        CompletableFuture<FileDataModel> dataModelFuture = command.execute();
        FileDataModel dataModel = dataModelFuture.get();

        assertTrue(dataModel instanceof Customer);
        Customer customer = (Customer) dataModel;
        assertEquals(123456789, customer.getCnpj());
        assertEquals("John", customer.getName());
        assertEquals("Vehicles", customer.getBusinessArea());
    }

    @Test
    public void testUnsuccessfulProcessing() throws ExecutionException, InterruptedException {
        when(applicationContext.getBean(anyString()))
                .thenReturn(new CustomerDataProcessor(applicationContext));
        command = new DataProcessingCommand("", applicationContext, Arrays.asList("002", "12345ab6789", "Mary", "Livestock"), executorService);

        CompletableFuture<FileDataModel> dataModelFuture = command.execute();
        FileDataModel dataModel = dataModelFuture.get();

        assertTrue(dataModel instanceof Salesman);
        Salesman salesman = (Salesman) dataModel;
        assertEquals(0, salesman.getCpf());
        assertEquals("", salesman.getName());
        assertEquals(0, salesman.getSalary());
    }
}