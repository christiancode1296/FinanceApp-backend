package FinanceApp.FinanceApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import FinanceApp.FinanceApp.repository.HistoricalDataRepository;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockDataServiceTest {

    @Autowired
    private StockDataService service;

    @MockitoBean
    private HistoricalDataRepository repository;

    @Test
    void testIsWeekend() {
        LocalDate saturday = LocalDate.of(2024, 1, 20);
        LocalDate sunday = LocalDate.of(2024, 1, 21);
        LocalDate monday = LocalDate.of(2024, 1, 22);

        assertTrue(service.isWeekend(saturday));
        assertTrue(service.isWeekend(sunday));
        assertFalse(service.isWeekend(monday));
    }

    @Test
    void testGetLastTradingDay() {
        LocalDate saturday = LocalDate.of(2024, 1, 20);
        LocalDate sunday = LocalDate.of(2024, 1, 21);
        LocalDate monday = LocalDate.of(2024, 1, 22);

        // Wenn Samstag, letzter Handelstag Freitag
        assertEquals(LocalDate.of(2024, 1, 19), service.getLastTradingDay(saturday));
        // Wenn Sonntag, letzter Handelstag Freitag
        assertEquals(LocalDate.of(2024, 1, 19), service.getLastTradingDay(sunday));
        // Wenn Montag, bleibt Montag (oder Freitag, je nach Implementierung)
        // Schauen wir uns die Implementierung an: 
        // return isWeekend(date) ? getLastTradingDay(date.minusDays(1)) : date;
        assertEquals(monday, service.getLastTradingDay(monday));
    }

    @Test
    void testShouldUpdateData() {
        LocalDate lastDate = LocalDate.of(2024, 1, 18); // Donnerstag
        LocalDate yesterday = LocalDate.of(2024, 1, 19); // Freitag
        
        // Update nötig wenn letztes Datum vor gestern liegt
        assertTrue(service.shouldUpdateData(lastDate, yesterday));
        
        // Kein Update nötig wenn letztes Datum gestern oder heute ist
        assertFalse(service.shouldUpdateData(yesterday, yesterday));
    }
}
