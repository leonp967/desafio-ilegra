package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultsProcessingCommandTest {

    private ResultsProcessingCommand command;
    private ApplicationContext applicationContext;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp(){
        applicationContext = mock(ApplicationContext.class);
        executorService = Executors.newFixedThreadPool(5);
        when(applicationContext.getBean(eq("result"), (Object[])any()))
                .thenAnswer(invocation -> new Result(invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
    }

    @Test
    public void testSuccessfulProcessing() throws ExecutionException, InterruptedException {
        List<Salesman> salesmen = new ArrayList<>();
        salesmen.add(new Salesman(1234577, "John", 2500));
        salesmen.add(new Salesman(1234577, "Michael", 2500));
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(88766172134L, "Larry", "Sales"));
        List<Sale> sales = new ArrayList<>();
        sales.add(new Sale(1, "John", Arrays.asList(new SaleItem(1, 2, 5), new SaleItem(2, 2, 25))));
        sales.add(new Sale(2, "Michael", Arrays.asList(new SaleItem(3, 2, 0.5), new SaleItem(4, 2, 2.5))));
        sales.add(new Sale(3, "John", Arrays.asList(new SaleItem(1, 5, 37), new SaleItem(2, 20, 25))));
        command = new ResultsProcessingCommand(applicationContext, "", salesmen, customers, sales, executorService);

        CompletableFuture<Result> resultFuture = command.execute();
        Result result = resultFuture.get();

        assertNotNull(result);
        assertEquals(2, result.getSalesmenAmount());
        assertEquals(1, result.getClientsAmount());
        assertEquals(3, result.getMostExpensiveSale());
        assertEquals("Michael", result.getWorstSalesman());
    }

    @Test
    public void testUnsuccessfulProcessing() throws ExecutionException, InterruptedException {
        command = new ResultsProcessingCommand(applicationContext, "", null, null, null, executorService);

        CompletableFuture<Result> resultFuture = command.execute();
        Result result = resultFuture.get();

        assertNotNull(result);
        assertEquals(0, result.getSalesmenAmount());
        assertEquals(0, result.getClientsAmount());
        assertEquals(0, result.getMostExpensiveSale());
        assertEquals("", result.getWorstSalesman());
    }
}
