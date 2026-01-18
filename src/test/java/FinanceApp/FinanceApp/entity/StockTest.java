package FinanceApp.FinanceApp.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void testStockConstructorAndGetters() {
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals("NASDAQ", stock.getExchange());
    }

    @Test
    void testStockSetters() {
        Stock stock = new Stock();
        stock.setSymbol("MSFT");
        stock.setName("Microsoft");
        stock.setExchange("NYSE");
        stock.setId(100L);

        assertEquals("MSFT", stock.getSymbol());
        assertEquals("Microsoft", stock.getName());
        assertEquals("NYSE", stock.getExchange());
        assertEquals(100L, stock.getId());
    }
}
