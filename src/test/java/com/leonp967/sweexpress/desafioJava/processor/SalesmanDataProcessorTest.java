package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SalesmanDataProcessorTest {

    private SalesmanDataProcessor processor;

    @BeforeEach
    public void setUp(){
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(eq("salesman"), (Object[])any()))
                .thenAnswer(invocation -> new Salesman(invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3)));
        processor = new SalesmanDataProcessor();
    }

    @Test
    public void testItReturnsCorrectSalesman(){
        FileDataModel response = processor.process(Arrays.asList("123456789", "Chief", "3650"));

        assertTrue(response instanceof Salesman);
        Salesman salesman = (Salesman) response;
        assertEquals(123456789L, salesman.getCpf());
        assertEquals("Chief", salesman.getName());
        assertEquals(3650, salesman.getSalary());
    }

    @Test
    public void testItThrowsNumberFormatException(){
        Throwable exception = assertThrows(NumberFormatException.class, () -> processor.process(Arrays.asList("123pot789", "Chief", "Vehicles")));

        assertNotNull(exception);
    }
}
