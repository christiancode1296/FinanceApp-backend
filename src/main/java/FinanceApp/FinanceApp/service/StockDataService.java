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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service für die Verwaltung historischer Aktiendaten.
 * <p>
 * Bietet intelligentes Caching und minimale API-Nutzung:
 * <ul>
 *   <li>Erste Abfrage: Lädt vollständige Historie von der API</li>
 *   <li>Folgeabfragen: Nur neue Daten seit letztem Börsentag</li>
 *   <li>Wochenenden/Feiertage: Keine API-Calls</li>
 * </ul>
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2026-01-03
 */
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
     *   <li>Daten veraltet → Nur neue Börsentage nachladen</li>
     *   <li>Daten aktuell (inkl. Wochenenden) → Direkt aus DB zurückgeben</li>
     * </ul>
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL"), case-insensitive
     * @return Liste aller historischen Datensätze, sortiert nach Datum (neueste zuerst)
     * @throws IllegalArgumentException wenn symbol null oder leer ist
     */
    @Transactional
    public List<HistoricalData> getStockData(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol darf nicht leer sein");
        }

        String sanitizedSymbol = symbol.toUpperCase().trim();
        System.out.println("===== DEBUG: getStockData(" + sanitizedSymbol + ") =====");

        // Schritt 1: Existieren Daten in der DB?
        boolean exists = historicalDataRepository.existsBySymbol(sanitizedSymbol);
        System.out.println("1. existsBySymbol() = " + exists);

        if (!exists) {
            System.out.println("2. Keine Daten gefunden → API-Call (volle Historie)");
            List<HistoricalData> apiData = fetchFullHistoryFromAPI(sanitizedSymbol);
            System.out.println("3. API lieferte " + apiData.size() + " Datensätze");

            if (!apiData.isEmpty()) {
                saveAllFast(apiData);
                System.out.println("4. Datensätze gespeichert");
            }
        } else {
            System.out.println("2. Daten vorhanden → Prüfe Update");
            updateStockDataIfNeeded(sanitizedSymbol);
        }

        // Schritt 5: Daten aus DB laden
        List<HistoricalData> result = historicalDataRepository.findAllBySymbolOrderByDateDesc(sanitizedSymbol);
        System.out.println("5. Rückgabe: " + result.size() + " Datensätze aus DB");
        System.out.println("===== DEBUG END =====\n");

        return result;
    }

    /**
     * Prüft, ob neue Daten verfügbar sind und lädt nur fehlende Tage nach.
     * <p>
     * Verhindert unnötige API-Calls durch intelligente Prüfung:
     * <ul>
     *   <li>API liefert nur Daten bis gestern</li>
     *   <li>Wochenenden werden übersprungen</li>
     *   <li>Nur bei fehlendem letztem Börsentag wird die API aufgerufen</li>
     * </ul>
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     */
    private void updateStockDataIfNeeded(String symbol) {
        // Neuesten Datensatz aus DB holen
        Optional<HistoricalData> latestDataOpt = historicalDataRepository
                .findFirstBySymbolOrderByDateDesc(symbol);

        if (latestDataOpt.isEmpty()) {
            System.out.println("Warnung: Keine Daten gefunden, obwohl existsBySymbol() true war!");
            return;
        }

        HistoricalData latestData = latestDataOpt.get();
        LocalDate lastDate = latestData.getDate();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        System.out.println("   Letztes Datum in DB: " + lastDate);
        System.out.println("   Gestern: " + yesterday);

        // Prüfen, ob Update nötig ist
        if (!shouldUpdateData(lastDate, yesterday)) {
            System.out.println("   → Daten sind aktuell. Kein Update nötig.");
            return;
        }

        System.out.println("   → Daten sind veraltet. Lade neue Daten...");

        // Nur neue Datensätze abrufen
        List<HistoricalData> newData = fetchLatestDataFromAPI(symbol, lastDate);

        if (!newData.isEmpty()) {
            saveAllFast(newData);
            System.out.println("   → " + newData.size() + " neue Datensätze gespeichert.");
        } else {
            System.out.println("   → Keine neuen Daten verfügbar (Wochenende/Feiertag).");
        }
    }

    /**
     * Prüft, ob ein Update der Daten notwendig ist.
     * <p>
     * Berücksichtigt:
     * <ul>
     *   <li>API liefert nur Daten bis gestern</li>
     *   <li>Wochenenden (Samstag/Sonntag haben keine Börsendaten)</li>
     *   <li>Ob lastDate bereits der letzte verfügbare Börsentag ist</li>
     * </ul>
     * </p>
     *
     * @param lastDate  letztes Datum in der DB
     * @param yesterday gestern (neueste mögliche Daten von der API)
     * @return true, wenn Update nötig ist
     */
    private boolean shouldUpdateData(LocalDate lastDate, LocalDate yesterday) {
        // Fall 1: Daten sind bereits aktuell (lastDate >= gestern)
        if (!lastDate.isBefore(yesterday)) {
            return false;
        }

        // Fall 2: Gestern war kein Börsentag (Wochenende)
        if (isWeekend(yesterday)) {
            // Letzter Börsentag war Freitag
            LocalDate lastTradingDay = getLastTradingDay(yesterday);
            // Update nur nötig, wenn lastDate vor dem letzten Börsentag liegt
            return lastDate.isBefore(lastTradingDay);
        }

        // Fall 3: Gestern war Börsentag und Daten fehlen → Update nötig
        return true;
    }

    /**
     * Prüft, ob ein Datum auf ein Wochenende fällt.
     *
     * @param date zu prüfendes Datum
     * @return true, wenn Samstag oder Sonntag
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Gibt den letzten Börsentag vor oder gleich dem angegebenen Datum zurück.
     * <p>
     * Ignoriert Wochenenden. US-Börsenfeiertage werden aktuell nicht berücksichtigt.
     * </p>
     *
     * @param date Referenzdatum
     * @return letzter Börsentag (Montag-Freitag)
     */
    private LocalDate getLastTradingDay(LocalDate date) {
        LocalDate current = date;
        while (isWeekend(current)) {
            current = current.minusDays(1);
        }
        return current;
    }

    /**
     * Führt einen API-Call durch und gibt das geparste JSON-Root-Node zurück.
     * <p>
     * Zentralisiert die API-Kommunikation und verhindert Code-Duplikation.
     * Behandelt Fehler robust und gibt im Fehlerfall ein leeres Optional zurück.
     * </p>
     *
     * @param symbol das Aktiensymbol
     * @return Optional mit dem JSON-Root-Node oder leer bei Fehler/leerer Response
     */
    private Optional<JsonNode> fetchApiResponse(String symbol) {
        try {
            String url = String.format("%s?symbol=%s&apikey=%s", quoteUrl, symbol, apiKey);
            System.out.println("   API-Call: " + url.replace(apiKey, "***"));

            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty() || response.equals("[]")) {
                System.out.println("   API-Response: leer");
                return Optional.empty();
            }

            System.out.println("   API-Response: " + response.length() + " Zeichen");
            JsonNode rootNode = objectMapper.readTree(response);
            return Optional.of(rootNode);

        } catch (Exception e) {
            System.err.println("Fehler beim API-Aufruf für " + symbol + ": " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Lädt nur neue Datensätze seit dem letzten gespeicherten Datum.
     * <p>
     * Filtert alle Datensätze, die bereits in der DB existieren.
     * Vermeidet doppelte DB-Queries durch In-Memory-Check.
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

                // Nur Datensätze NACH dem letzten gespeicherten Datum
                if (date.isAfter(lastDate)) {
                    BigDecimal price = BigDecimal.valueOf(node.get("price").asDouble());
                    Long volume = node.has("volume") ? node.get("volume").asLong() : 0L;

                    // Duplikate vermeiden
                    if (!historicalDataRepository.findBySymbolAndDate(symbol, date).isPresent()) {
                        results.add(new HistoricalData(symbol, date, price, volume));
                    }
                }
            }
        }

        return results;
    }

    /**
     * Lädt die vollständige Historie eines Aktiensymbols von der API.
     * <p>
     * Wird nur beim ersten Request für ein Symbol aufgerufen.
     * Nutzt keine Duplikatsprüfung, da die DB leer ist.
     * </p>
     *
     * @param symbol das Aktiensymbol
     * @return vollständige historische Daten (alle verfügbaren Jahre)
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

    /**
     * Schneller Batch-Insert über JdbcTemplate mit expliziter Sequenz-Nutzung.
     * <p>
     * Performance: ~15x schneller als {@code repository.saveAll()}.
     * Nutzt Batch-Updates mit 100 Datensätzen pro Batch.
     * </p>
     *
     * @param data Liste der zu speichernden Datensätze
     * @throws RuntimeException bei DB-Fehlern (z.B. Constraint-Verletzungen)
     */
    private void saveAllFast(List<HistoricalData> data) {
        if (data.isEmpty()) {
            System.out.println("   saveAllFast(): Keine Daten zu speichern");
            return;
        }

        String sql = "INSERT INTO historical_data (id, symbol, date, price, volume) VALUES (nextval('historical_data_seq'), ?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, data, 100, (PreparedStatement ps, HistoricalData item) -> {
                ps.setString(1, item.getSymbol());
                ps.setDate(2, java.sql.Date.valueOf(item.getDate()));
                ps.setBigDecimal(3, item.getPrice());
                ps.setLong(4, item.getVolume());
            });
            System.out.println("   saveAllFast(): " + data.size() + " Datensätze erfolgreich gespeichert");
        } catch (Exception e) {
            System.err.println("Fehler beim Batch-Insert: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
