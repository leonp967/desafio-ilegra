package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SalesDataProcessorTest {

    private SalesDataProcessor processor;

    @BeforeEach
    public void setUp(){
        processor = new SalesDataProcessor("-", ",");
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
