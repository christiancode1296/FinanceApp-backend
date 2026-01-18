package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StockRepositoryTest {

    @Autowired
    private StockRepository repository;

    @Test
    void testSaveAndFindBySymbol() {
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        repository.save(stock);

        Optional<Stock> found = repository.findBySymbol("AAPL");
        assertTrue(found.isPresent());
        assertEquals("Apple Inc.", found.get().getName());
    }

    @Test
    void testSearchStocks() {
        repository.save(new Stock("AAPL", "Apple Inc.", "NASDAQ"));
        repository.save(new Stock("MSFT", "Microsoft", "NASDAQ"));
        repository.save(new Stock("GOOGL", "Alphabet", "NASDAQ"));

        List<Stock> result = repository.searchStocks("App");
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(s -> s.getSymbol().equals("AAPL")));
        
        List<Stock> result2 = repository.searchStocks("Micro");
        assertEquals(1, result2.size());
        assertEquals("MSFT", result2.get(0).getSymbol());
    }
}
