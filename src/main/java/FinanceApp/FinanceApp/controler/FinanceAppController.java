package FinanceApp.FinanceApp.controler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stocks")
public class FinanceAppController {

    @Value("${fmp.api.key}")
    private String apiKey;

    @Value("${fmp.base.url}")
    private String baseUrl; // jetzt korrekt: https://financialmodelingprep.com/stable

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
    @GetMapping("/search")
    public ResponseEntity<String> searchStocks(@RequestParam String query) {
        try {
            String url = String.format(
                    "%s/search-name?query=%s&apikey=%s",
                    baseUrl, query, apiKey
            );
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

}
