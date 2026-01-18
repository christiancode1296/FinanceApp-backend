package FinanceApp.FinanceApp.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class HistoricalDataTest {

    @Test
    void testHistoricalDataConstructorAndGetters() {
        LocalDate now = LocalDate.now();
        BigDecimal price = new BigDecimal("150.50");
        HistoricalData data = new HistoricalData("AAPL", now, price, 1000000L);

        assertEquals("AAPL", data.getSymbol());
        assertEquals(now, data.getDate());
        assertEquals(price, data.getPrice());
        assertEquals(1000000L, data.getVolume());
    }

    @Test
    void testHistoricalDataSetters() {
        HistoricalData data = new HistoricalData();
        LocalDate date = LocalDate.of(2023, 1, 1);
        BigDecimal price = new BigDecimal("200.00");
        
        data.setId(1L);
        data.setSymbol("GOOGL");
        data.setDate(date);
        data.setPrice(price);
        data.setVolume(500000L);

        assertEquals(1L, data.getId());
        assertEquals("GOOGL", data.getSymbol());
        assertEquals(date, data.getDate());
        assertEquals(price, data.getPrice());
        assertEquals(500000L, data.getVolume());
    }
}
