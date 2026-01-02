package FinanceApp.FinanceApp.service;

import FinanceApp.FinanceApp.entity.HistoricalData;
import FinanceApp.FinanceApp.repository.HistoricalDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockDataService {

    @Autowired
    private HistoricalDataRepository historicalDataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${fmp.quote.url}")
    private String quoteUrl;

    @Value("${fmp.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public List<HistoricalData> getStockData(String symbol) {
        String sanitizedSymbol = symbol.toUpperCase().trim();

        if (!historicalDataRepository.existsBySymbol(sanitizedSymbol)) {
            List<HistoricalData> apiData = fetchFullHistoryFromAPI(sanitizedSymbol);

            if (!apiData.isEmpty()) {
                // Schneller Batch-Insert über JDBC
                saveAllFast(apiData);
            }
        }
        return historicalDataRepository.findAllBySymbolOrderByDateDesc(sanitizedSymbol);
    }

    private void saveAllFast(List<HistoricalData> data) {
        String sql = "INSERT INTO historical_data (id, symbol, date, price, volume) VALUES (nextval('historical_data_seq'), ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, data, 100, (PreparedStatement ps, HistoricalData item) -> {
            ps.setString(1, item.getSymbol());
            ps.setDate(2, java.sql.Date.valueOf(item.getDate()));
            ps.setBigDecimal(3, item.getPrice());
            ps.setLong(4, item.getVolume());
        });
    }

    private List<HistoricalData> fetchFullHistoryFromAPI(String symbol) {
        List<HistoricalData> results = new ArrayList<>();
        try {
            // Wir bleiben bei deiner funktionierenden URL-Struktur
            String url = String.format("%s?symbol=%s&apikey=%s", quoteUrl, symbol, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty() || response.equals("[]")) return results;

            JsonNode rootNode = objectMapper.readTree(response);

            // Da du sagst, du bekommst alle Datensätze, iterieren wir über das Array
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    LocalDate date = LocalDate.parse(node.get("date").asText());
                    // Wir nutzen 'price' wie in deinem API-Bild gesehen
                    BigDecimal price = BigDecimal.valueOf(node.get("price").asDouble());
                    Long volume = node.has("volume") ? node.get("volume").asLong() : 0L;

                    results.add(new HistoricalData(symbol, date, price, volume));
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der historischen Daten: " + e.getMessage());
        }
        return results;
    }
}