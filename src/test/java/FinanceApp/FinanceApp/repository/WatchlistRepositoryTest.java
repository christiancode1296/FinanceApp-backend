package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.WatchlistItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WatchlistRepositoryTest {

    @Autowired
    private WatchlistRepository repository;

    @Test
    void testSaveAndFindByUserId() {
        WatchlistItem item1 = new WatchlistItem();
        item1.setUserId("user1");
        item1.setSymbol("AAPL");
        repository.save(item1);

        WatchlistItem item2 = new WatchlistItem();
        item2.setUserId("user1");
        item2.setSymbol("MSFT");
        repository.save(item2);

        WatchlistItem item3 = new WatchlistItem();
        item3.setUserId("user2");
        item3.setSymbol("TSLA");
        repository.save(item3);

        List<WatchlistItem> user1Items = repository.findByUserId("user1");
        assertEquals(2, user1Items.size());
        
        List<WatchlistItem> user2Items = repository.findByUserId("user2");
        assertEquals(1, user2Items.size());
        assertEquals("TSLA", user2Items.get(0).getSymbol());
    }

    @Test
    void testDeleteByUserIdAndSymbol() {
        WatchlistItem item = new WatchlistItem();
        item.setUserId("user1");
        item.setSymbol("AAPL");
        repository.save(item);

        repository.deleteByUserIdAndSymbol("user1", "AAPL");
        
        List<WatchlistItem> result = repository.findByUserId("user1");
        assertTrue(result.isEmpty());
    }
}
