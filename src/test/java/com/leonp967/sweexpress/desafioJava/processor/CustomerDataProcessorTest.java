package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerDataProcessorTest {

    private CustomerDataProcessor processor;

    @BeforeEach
    public void setUp(){
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(eq("customer"), (Object[])any()))
                .thenAnswer(invocation -> new Customer(invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3)));
        processor = new CustomerDataProcessor(applicationContext);
    }

    @Test
    public void testItReturnsCorrectCustomer(){
        FileDataModel response = processor.process(Arrays.asList("123456789", "Chief", "Vehicles"));

        assertTrue(response instanceof Customer);
        Customer customer = (Customer) response;
        assertEquals(123456789L, customer.getCnpj());
        assertEquals("Chief", customer.getName());
        assertEquals("Vehicles", customer.getBusinessArea());
    }

    @Test
    public void testItThrowsNumberFormatException(){
        Throwable exception = assertThrows(NumberFormatException.class, () -> processor.process(Arrays.asList("123pot789", "Chief", "Vehicles")));

        assertNotNull(exception);
    }
}
