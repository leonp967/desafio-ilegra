package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.bo.DataProcessingBO;
import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.SaleItem;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class ResultsProcessingCommandTest {

    private ResultsProcessingCommand command;
    private ApplicationContext applicationContext;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp(){
        applicationContext = mock(ApplicationContext.class);
        executorService = Executors.newFixedThreadPool(5);
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

        DataProcessingBO dataProcessingBO = DataProcessingBO.builder()
                .salesmen(salesmen)
                .customers(customers)
                .sales(sales)
                .build();
        command = new ResultsProcessingCommand("", dataProcessingBO);

        command.observe().subscribe(result -> {
            assertNotNull(result);
            assertEquals(2, result.getSalesmenAmount());
            assertEquals(1, result.getClientsAmount());
            assertEquals(3, result.getMostExpensiveSale());
            assertEquals("Michael", result.getWorstSalesman());
        });
    }

    @Test
    public void testUnsuccessfulProcessing() throws ExecutionException, InterruptedException {
        command = new ResultsProcessingCommand("", DataProcessingBO.builder().build());

        command.observe().subscribe(result -> {
            assertNotNull(result);
            assertEquals(0, result.getSalesmenAmount());
            assertEquals(0, result.getClientsAmount());
            assertEquals(0, result.getMostExpensiveSale());
            assertEquals("", result.getWorstSalesman());
        });
    }
}
