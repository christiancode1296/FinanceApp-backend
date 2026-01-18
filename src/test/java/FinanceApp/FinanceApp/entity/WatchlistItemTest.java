package FinanceApp.FinanceApp.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WatchlistItemTest {

    @Test
    void testWatchlistItemGettersAndSetters() {
        WatchlistItem item = new WatchlistItem();
        item.setId(1L);
        item.setUserId("user123");
        item.setSymbol("TSLA");

        assertEquals(1L, item.getId());
        assertEquals("user123", item.getUserId());
        assertEquals("TSLA", item.getSymbol());
    }
}
