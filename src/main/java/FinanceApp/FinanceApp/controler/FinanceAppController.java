package FinanceApp.FinanceApp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stocks")
public class FinanceAppController  {

    @Value("${fmp.api.key}")
    private String apiKey;

    @Value("${fmp.base.url}")
    private String baseUrl; // z.B. https://financialmodelingprep.com/api/v3

    @GetMapping("/{symbol}")
    public ResponseEntity<String> getStockData(@PathVariable String symbol) {
        try {
            String url = String.format(
                    "https://financialmodelingprep.com/stable/historical-price-eod/full?symbol=%s&apikey=%s",
                    symbol, apiKey  // apiKey kommt aus application.properties wie besprochen
            );
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // optional: zum schnellen Prüfen, ob Backend läuft
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\"}");
    }
}