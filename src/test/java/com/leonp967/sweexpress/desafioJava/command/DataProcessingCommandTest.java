package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.factory.ProcessorFactory;
import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataProcessingCommandTest {

    private DataProcessingCommand command;

    @BeforeEach
    public void setUp(){

    }

    @Test
    public void testSuccessfulProcessing() {
        command = new DataProcessingCommand("", Arrays.asList("002", "123456789", "John", "Vehicles"));
        ProcessorFactory processorFactory = spy(ProcessorFactory.class);
        ReflectionTestUtils.setField(command, "processorFactory", processorFactory);

        CustomerDataProcessor customerDataProcessor = mock(CustomerDataProcessor.class);
        when(customerDataProcessor.process(any()))
                .thenReturn(new Customer(123456789, "John", "Vehicles"));

        command.observe().subscribe(dataModel -> {
            assertTrue(dataModel instanceof Customer);
            Customer customer = (Customer) dataModel;
            assertEquals(123456789, customer.getCnpj());
            assertEquals("John", customer.getName());
            assertEquals("Vehicles", customer.getBusinessArea());
        });
    }

    @Test
    public void testUnsuccessfulProcessing() {
        command = new DataProcessingCommand("", Arrays.asList("002", "12345ab6789", "Mary", "Livestock"));
        ProcessorFactory processorFactory = spy(ProcessorFactory.class);
        ReflectionTestUtils.setField(command, "processorFactory", processorFactory);

        command.observe().subscribe(dataModel -> {
            assertTrue(dataModel instanceof Customer);
            Customer customer = (Customer) dataModel;
            assertThat(customer.getBusinessArea(), blankOrNullString());
            assertThat(customer.getCnpj(), is(0L));
            assertThat(customer.getName(), blankOrNullString());
        });
    }
}