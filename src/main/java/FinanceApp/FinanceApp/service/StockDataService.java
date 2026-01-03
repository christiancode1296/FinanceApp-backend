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
import java.util.Optional;

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

    /**
     * Ruft Aktiendaten ab und speichert neue/fehlende Datensätze.
     * <p>
     * Intelligente Logik:
     * <ul>
     *   <li>Keine Daten vorhanden → Volle Historie laden</li>
     *   <li>Daten veraltet (nicht heute) → Nur neue Tage nachladen</li>
     *   <li>Daten aktuell → Direkt aus DB zurückgeben</li>
     * </ul>
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     * @return Liste aller historischen Datensätze, sortiert nach Datum (neueste zuerst)
     */
    @Transactional
    public List<HistoricalData> getStockData(String symbol) {
        String sanitizedSymbol = symbol.toUpperCase().trim();

        // Fall 1: Noch keine Daten in DB → Volle Historie laden
        if (!historicalDataRepository.existsBySymbol(sanitizedSymbol)) {
            List<HistoricalData> apiData = fetchFullHistoryFromAPI(sanitizedSymbol);

            if (!apiData.isEmpty()) {
                saveAllFast(apiData);
            }
        }
        // Fall 2: Daten vorhanden → Prüfen, ob Update nötig
        else {
            updateStockDataIfNeeded(sanitizedSymbol);
        }

        return historicalDataRepository.findAllBySymbolOrderByDateDesc(sanitizedSymbol);
    }

    /**
     * Prüft, ob neue Daten verfügbar sind und lädt nur fehlende Tage nach.
     * <p>
     * Verhindert unnötige API-Calls, wenn bereits aktuelle Daten vorliegen.
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     */
    private void updateStockDataIfNeeded(String symbol) {
        // Neuesten Datensatz aus DB holen
        Optional<HistoricalData> latestDataOpt = historicalDataRepository
                .findFirstBySymbolOrderByDateDesc(symbol);

        if (latestDataOpt.isEmpty()) return;

        HistoricalData latestData = latestDataOpt.get();
        LocalDate lastDate = latestData.getDate();
        LocalDate today = LocalDate.now();

        // Prüfen, ob Daten veraltet sind
        if (lastDate.isBefore(today)) {
            System.out.println("Daten für " + symbol + " sind veraltet (letztes Datum: " + lastDate + "). Lade neue Daten...");

            // Nur neue Datensätze abrufen
            List<HistoricalData> newData = fetchLatestDataFromAPI(symbol, lastDate);

            if (!newData.isEmpty()) {
                saveAllFast(newData);
                System.out.println(newData.size() + " neue Datensätze für " + symbol + " gespeichert.");
            }
        } else {
            System.out.println("Daten für " + symbol + " sind aktuell (letztes Datum: " + lastDate + ").");
        }
    }


    /**
     * Führt einen API-Call durch und gibt das geparste JSON-Root-Node zurück.
     * <p>
     * Zentralisiert die API-Kommunikation und verhindert Code-Duplikation.
     * </p>
     *
     * @param symbol das Aktiensymbol
     * @return Optional mit dem JSON-Root-Node oder leer bei Fehler/leerer Response
     */
    private Optional<JsonNode> fetchApiResponse(String symbol) {
        try {
            String url = String.format("%s?symbol=%s&apikey=%s", quoteUrl, symbol, apiKey);
            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty() || response.equals("[]")) {
                return Optional.empty();
            }

            JsonNode rootNode = objectMapper.readTree(response);
            return Optional.of(rootNode);

        } catch (Exception e) {
            System.err.println("Fehler beim API-Aufruf für " + symbol + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Lädt nur neue Datensätze seit dem letzten gespeicherten Datum.
     * <p>
     * Filtert alle Datensätze, die bereits in der DB existieren.
     * </p>
     *
     * @param symbol   das Aktiensymbol
     * @param lastDate letztes gespeichertes Datum in der DB
     * @return Liste neuer Datensätze (nach lastDate)
     */
    private List<HistoricalData> fetchLatestDataFromAPI(String symbol, LocalDate lastDate) {
        List<HistoricalData> results = new ArrayList<>();

        Optional<JsonNode> rootNodeOpt = fetchApiResponse(symbol);
        if (rootNodeOpt.isEmpty()) return results;

        JsonNode rootNode = rootNodeOpt.get();

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                LocalDate date = LocalDate.parse(node.get("date").asText());

                if (date.isAfter(lastDate)) {
                    BigDecimal price = BigDecimal.valueOf(node.get("price").asDouble());
                    Long volume = node.has("volume") ? node.get("volume").asLong() : 0L;

                    if (!historicalDataRepository.findBySymbolAndDate(symbol, date).isPresent()) {
                        results.add(new HistoricalData(symbol, date, price, volume));
                    }
                }
            }
        }

        return results;
    }

    /**
     * Schneller Batch-Insert über JdbcTemplate.
     * <p>
     * Performance: ~15x schneller als {@code repository.saveAll()}.
     * </p>
     *
     * @param data Liste der zu speichernden Datensätze
     */
    private void saveAllFast(List<HistoricalData> data) {
        String sql = "INSERT INTO historical_data (id, symbol, date, price, volume) VALUES (nextval('historical_data_seq'), ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, data, 100, (PreparedStatement ps, HistoricalData item) -> {
            ps.setString(1, item.getSymbol());
            ps.setDate(2, java.sql.Date.valueOf(item.getDate()));
            ps.setBigDecimal(3, item.getPrice());
            ps.setLong(4, item.getVolume());
        });
    }

    /**
     * Lädt die vollständige Historie eines Aktiensymbols von der API.
     * <p>
     * Wird nur beim ersten Request für ein Symbol aufgerufen.
     * </p>
     *
     * @param symbol das Aktiensymbol
     * @return vollständige historische Daten
     */
    private List<HistoricalData> fetchFullHistoryFromAPI(String symbol) {
        List<HistoricalData> results = new ArrayList<>();

        Optional<JsonNode> rootNodeOpt = fetchApiResponse(symbol);
        if (rootNodeOpt.isEmpty()) return results;

        JsonNode rootNode = rootNodeOpt.get();

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                LocalDate date = LocalDate.parse(node.get("date").asText());
                BigDecimal price = BigDecimal.valueOf(node.get("price").asDouble());
                Long volume = node.has("volume") ? node.get("volume").asLong() : 0L;

                results.add(new HistoricalData(symbol, date, price, volume));
            }
        }

        return results;
    }
}
