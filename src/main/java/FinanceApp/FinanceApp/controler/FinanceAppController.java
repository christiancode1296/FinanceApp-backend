package FinanceApp.FinanceApp.controler;

import FinanceApp.FinanceApp.entity.Stock;
import FinanceApp.FinanceApp.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class FinanceAppController {

    @Autowired
    private StockRepository stockRepository;

    @Value("${fmp.api.key}")
    private String apiKey;

    @Value("${fmp.base.url}")
    private String baseUrl;

    @GetMapping("/{symbol}")
    public ResponseEntity<String> getStockData(@PathVariable String symbol) {
        try {
            String url = String.format("%s/historical-price-eod/full?symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
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
