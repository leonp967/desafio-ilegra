package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.SaleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SalesDataProcessorTest {

    private SalesDataProcessor processor;

    @BeforeEach
    public void setUp(){
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(eq("sale"), (Object[])any()))
                .thenAnswer(invocation -> new Sale(invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3)));
        when(applicationContext.getBean(eq("saleItem"), (Object[])any()))
                .thenAnswer(invocation -> new SaleItem(invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3)));
        processor = new SalesDataProcessor(applicationContext, "-", ",");
    }

    @Test
    public void testItReturnsCorrectSale(){
        FileDataModel response = processor.process(Arrays.asList("1", "[1-10-100,2-30-2.50]", "Trevor"));

        assertTrue(response instanceof Sale);
        Sale sale = (Sale) response;
        assertEquals(1, sale.getId());
        assertEquals("Trevor", sale.getSalesmanName());
        assertEquals(1, sale.getItemsList().get(0).getId());
        assertEquals(100, sale.getItemsList().get(0).getPrice());
        assertEquals(10, sale.getItemsList().get(0).getQuantity());
        assertEquals(2, sale.getItemsList().get(1).getId());
        assertEquals(2.50, sale.getItemsList().get(1).getPrice());
        assertEquals(30, sale.getItemsList().get(1).getQuantity());
    }

    @Test
    public void testItThrowsNumberFormatException(){
        Throwable exception = assertThrows(NumberFormatException.class, () -> processor.process(Arrays.asList("1", "[1-10a-100,2-30-2.50]", "Trevor")));

        assertNotNull(exception);
    }
}
