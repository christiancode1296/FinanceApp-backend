package FinanceApp.FinanceApp.controller;

import FinanceApp.FinanceApp.controller.WatchlistController;
import FinanceApp.FinanceApp.entity.WatchlistItem;
import FinanceApp.FinanceApp.repository.WatchlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WatchlistController.class)
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WatchlistRepository repository;

    @Test
    void testGetWatchlist() throws Exception {
        WatchlistItem item = new WatchlistItem();
        item.setUserId("user1");
        item.setSymbol("AAPL");

        when(repository.findByUserId("user1")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/watchlist/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }

    @Test
    void testAddToWatchlist() throws Exception {
        WatchlistItem item = new WatchlistItem();
        item.setSymbol("MSFT");

        when(repository.save(any(WatchlistItem.class))).thenReturn(item);

        mockMvc.perform(post("/api/watchlist/user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"MSFT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("MSFT"));
    }

    @Test
    void testRemoveFromWatchlist() throws Exception {
        mockMvc.perform(delete("/api/watchlist/user1/AAPL"))
                .andExpect(status().isOk());
    }
}
