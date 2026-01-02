package FinanceApp.FinanceApp.controler;

import FinanceApp.FinanceApp.entity.HistoricalData;
import FinanceApp.FinanceApp.entity.Stock;
import FinanceApp.FinanceApp.repository.StockRepository;
import FinanceApp.FinanceApp.service.StockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class FinanceAppController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockDataService stockDataService;

    @GetMapping("/{symbol}")
    public ResponseEntity<List<HistoricalData>> getStockData(@PathVariable String symbol) {
        // Fix: Ändere den Typ von HistoricalData zu List<HistoricalData>
        List<HistoricalData> data = stockDataService.getStockData(symbol);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String query) {
        List<Stock> results = stockRepository.searchStocks(query);
        return ResponseEntity.ok(results);
    }
}

