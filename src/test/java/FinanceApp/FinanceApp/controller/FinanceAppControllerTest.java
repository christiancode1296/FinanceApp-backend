package FinanceApp.FinanceApp.controller;

import FinanceApp.FinanceApp.controller.FinanceAppController;
import FinanceApp.FinanceApp.entity.Stock;
import FinanceApp.FinanceApp.repository.StockRepository;
import FinanceApp.FinanceApp.service.StockDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FinanceAppController.class)
class FinanceAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockRepository stockRepository;

    @MockitoBean
    private StockDataService stockDataService;

    @Test
    void testGetAllStocks() throws Exception {
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        when(stockRepository.findAll()).thenReturn(List.of(stock));

        mockMvc.perform(get("/api/stocks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }

    @Test
    void testSearchStocks() throws Exception {
        Stock stock = new Stock("MSFT", "Microsoft", "NASDAQ");
        when(stockRepository.searchStocks("Micro")).thenReturn(List.of(stock));

        mockMvc.perform(get("/api/stocks/search").param("query", "Micro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("MSFT"));
    }
}
