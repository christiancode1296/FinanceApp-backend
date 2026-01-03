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

/**
 * REST-Controller für die Verwaltung von Aktiendaten in der Finance-Anwendung.
 *
 * <h2>Was ist ein Controller? 🎯</h2>
 * <p>Ein Controller ist die "Empfangsstelle" Ihrer Anwendung. Er funktioniert wie ein Kellner
 * im Restaurant:</p>
 * <ul>
 *   <li>Nimmt Bestellungen entgegen (HTTP-Requests vom Frontend)</li>
 *   <li>Geht in die Küche (ruft Service/Repository auf)</li>
 *   <li>Bringt das Essen zurück (sendet Response an Frontend)</li>
 * </ul>
 *
 * <h2>Basis-URL: /api/stocks</h2>
 * <p>Alle Endpoints in diesem Controller beginnen mit <code>/api/stocks</code></p>
 *
 * <h2>Verfügbare Endpoints: 📍</h2>
 * <table border="1">
 *   <tr>
 *     <th>URL</th>
 *     <th>Methode</th>
 *     <th>Zweck</th>
 *     <th>Beispiel</th>
 *   </tr>
 *   <tr>
 *     <td>/api/stocks/{symbol}</td>
 *     <td>GET</td>
 *     <td>Historische Kursdaten einer Aktie abrufen</td>
 *     <td>/api/stocks/AAPL</td>
 *   </tr>
 *   <tr>
 *     <td>/api/stocks/all</td>
 *     <td>GET</td>
 *     <td>Alle gespeicherten Aktien abrufen</td>
 *     <td>/api/stocks/all</td>
 *   </tr>
 *   <tr>
 *     <td>/api/stocks/search?query=...</td>
 *     <td>GET</td>
 *     <td>Aktien nach Name/Symbol suchen</td>
 *     <td>/api/stocks/search?query=Apple</td>
 *   </tr>
 * </table>
 *
 * <h2>Datenquelle:</h2>
 * <p>Die Aktiendaten werden von der <b>Financial Modeling Prep (FMP) API</b> bezogen.</p>
 * <ul>
 *   <li><b>API-Provider:</b> Financial Modeling Prep (financialmodelingprep.com)</li>
 *   <li><b>Datentypen:</b> Historische Kursdaten, Realtime-Quotes, Fundamentaldaten</li>
 *   <li><b>Authentifizierung:</b> API-Key erforderlich (konfiguriert in application.properties)</li>
 * </ul>
 *
 * <h2>Verwendete Technologien:</h2>
 * <ul>
 *   <li><b>@RestController:</b> Markiert diese Klasse als REST-API-Controller (gibt JSON zurück)</li>
 *   <li><b>@RequestMapping:</b> Definiert die Basis-URL für alle Endpoints</li>
 *   <li><b>@Autowired:</b> Spring erstellt automatisch Instanzen (Dependency Injection)</li>
 *   <li><b>ResponseEntity:</b> Ermöglicht präzise HTTP-Antworten mit Statuscode</li>
 * </ul>
 *
 * @author Christian
 * @version 1.0
 * @since 1.0
 * @see Stock
 * @see HistoricalData
 * @see StockRepository
 * @see StockDataService
 */
@RestController
@RequestMapping("/api/stocks")
public class FinanceAppController {

    /**
     * Repository für Datenbankzugriff auf Stock-Entitäten.
     *
     * <p><b>Was ist ein Repository? 💾</b></p>
     * <p>Ein Repository ist wie ein Lagerverwalter - es kümmert sich um das Speichern,
     * Laden und Suchen von Daten in der Datenbank.</p>
     *
     * <p><b>@Autowired erklärt:</b></p>
     * <p>Spring erstellt automatisch eine Instanz von StockRepository. Sie müssen
     * nicht selbst <code>new StockRepository()</code> schreiben. Das nennt man
     * "Dependency Injection" (Abhängigkeitsinjektion).</p>
     */
    @Autowired
    private StockRepository stockRepository;

    /**
     * Service für das Abrufen von Aktiendaten aus der Financial Modeling Prep (FMP) API.
     *
     * <p><b>Was ist ein Service? ⚙️</b></p>
     * <p>Ein Service enthält die Geschäftslogik - in diesem Fall kommuniziert er mit
     * der FMP API, um aktuelle und historische Aktiendaten abzurufen.</p>
     *
     * <p><b>Unterschied Repository vs. Service:</b></p>
     * <ul>
     *   <li><b>Repository:</b> Zugriff auf IHRE lokale Datenbank (gespeicherte Aktien)</li>
     *   <li><b>Service:</b> Geschäftslogik und externe API-Aufrufe (FMP API)</li>
     * </ul>
     *
     * <p><b>FMP API Features:</b></p>
     * <ul>
     *   <li>Historische Kursdaten (täglich, wöchentlich, monatlich)</li>
     *   <li>Realtime Stock Quotes</li>
     *   <li>Fundamentaldaten (Bilanzen, Gewinn- und Verlustrechnungen)</li>
     *   <li>Marktindikatoren und Indizes</li>
     * </ul>
     */
    @Autowired
    private StockDataService stockDataService;

    /**
     * Ruft historische Kursdaten für eine bestimmte Aktie von der FMP API ab.
     *
     * <h3>So funktioniert dieser Endpoint: 🔍</h3>
     * <ol>
     *   <li>Frontend sendet Request: <code>GET http://localhost:8080/api/stocks/AAPL</code></li>
     *   <li>Spring extrahiert "AAPL" aus der URL und speichert es in <code>symbol</code></li>
     *   <li>Controller ruft <code>stockDataService.getStockData("AAPL")</code> auf</li>
     *   <li>Service sendet Request an FMP API:
     *       <code>https://financialmodelingprep.com/api/v3/historical-price-full/AAPL?apikey=...</code></li>
     *   <li>FMP API liefert historische Kursdaten zurück</li>
     *   <li>Service verarbeitet die Daten und konvertiert sie in HistoricalData-Objekte</li>
     *   <li>Controller sendet Daten als JSON an Frontend zurück</li>
     * </ol>
     *
     * <h3>Annotationen erklärt:</h3>
     * <ul>
     *   <li><b>@GetMapping("/{symbol}"):</b>
     *       Reagiert auf GET-Requests. <code>{symbol}</code> ist ein Platzhalter
     *       für das Aktiensymbol (z.B. AAPL, TSLA, MSFT)</li>
     *   <li><b>@PathVariable String symbol:</b>
     *       Nimmt den Wert aus der URL und speichert ihn in der Variable <code>symbol</code></li>
     * </ul>
     *
     * <h3>FMP API Endpoint:</h3>
     * <p>Dieser Controller-Endpoint nutzt intern die FMP API:</p>
     * <pre>
     * GET https://financialmodelingprep.com/api/v3/historical-price-full/{symbol}?apikey={API_KEY}
     * </pre>
     *
     * <h3>Beispiel-Request und Response:</h3>
     * <pre>
     * Request:  GET /api/stocks/AAPL
     *
     * Response: HTTP 200 OK
     * [
     *   {
     *     "date": "2024-01-15",
     *     "open": 185.50,
     *     "close": 187.20,
     *     "high": 188.00,
     *     "low": 184.80,
     *     "volume": 45000000
     *   },
     *   ...
     * ]
     * </pre>
     *
     * @param symbol Das Aktiensymbol (z.B. "AAPL" für Apple, "TSLA" für Tesla)
     * @return ResponseEntity mit Liste von HistoricalData-Objekten und HTTP-Status 200 (OK)
     * @throws RuntimeException wenn das Symbol nicht gefunden wird, FMP API-Fehler auftritt oder API-Limit erreicht ist
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<List<HistoricalData>> getStockData(@PathVariable String symbol) {
        // Service-Aufruf: Holt historische Daten für das angegebene Symbol von FMP API
        List<HistoricalData> data = stockDataService.getStockData(symbol);

        // ResponseEntity.ok() erstellt eine HTTP 200 (OK) Antwort mit den Daten als JSON
        return ResponseEntity.ok(data);
    }

    /**
     * Ruft alle gespeicherten Aktien aus der lokalen Datenbank ab.
     *
     * <h3>So funktioniert dieser Endpoint: 📋</h3>
     * <ol>
     *   <li>Frontend sendet Request: <code>GET http://localhost:8080/api/stocks/all</code></li>
     *   <li>Controller ruft <code>stockRepository.findAll()</code> auf</li>
     *   <li>Repository holt ALLE Stock-Einträge aus der lokalen Datenbank</li>
     *   <li>Controller sendet die Liste als JSON zurück</li>
     * </ol>
     *
     * <h3>Annotationen erklärt:</h3>
     * <ul>
     *   <li><b>@GetMapping("/all"):</b>
     *       Reagiert auf GET-Requests an <code>/api/stocks/all</code></li>
     * </ul>
     *
     * <h3>Datenquelle:</h3>
     * <p><b>Wichtig:</b> Dieser Endpoint ruft NICHT die FMP API auf, sondern liefert nur
     * die in Ihrer lokalen Datenbank gespeicherten Aktien zurück. Die Daten wurden
     * möglicherweise zuvor von der FMP API abgerufen und lokal gespeichert.</p>
     *
     * <h3>Verwendungszweck:</h3>
     * <p>Dieser Endpoint wird typischerweise verwendet, um eine Übersicht
     * aller verfügbaren/favorisierten Aktien anzuzeigen (z.B. in einer Dropdown-Liste,
     * Watchlist oder Tabelle im Frontend).</p>
     *
     * <h3>Beispiel-Request und Response:</h3>
     * <pre>
     * Request:  GET /api/stocks/all
     *
     * Response: HTTP 200 OK
     * [
     *   {
     *     "id": 1,
     *     "symbol": "AAPL",
     *     "name": "Apple Inc.",
     *     "exchange": "NASDAQ"
     *   },
     *   {
     *     "id": 2,
     *     "symbol": "TSLA",
     *     "name": "Tesla Inc.",
     *     "exchange": "NASDAQ"
     *   }
     * ]
     * </pre>
     *
     * @return ResponseEntity mit Liste aller Stock-Objekte aus der lokalen DB und HTTP-Status 200 (OK)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        // Repository-Aufruf: Holt alle Aktien aus der lokalen Datenbank
        return ResponseEntity.ok(stockRepository.findAll());
    }

    /**
     * Sucht nach Aktien basierend auf einem Suchbegriff in der lokalen Datenbank.
     *
     * <h3>So funktioniert dieser Endpoint: 🔎</h3>
     * <ol>
     *   <li>Frontend sendet Request: <code>GET http://localhost:8080/api/stocks/search?query=Apple</code></li>
     *   <li>Spring extrahiert "Apple" aus dem Query-Parameter und speichert es in <code>query</code></li>
     *   <li>Controller ruft <code>stockRepository.searchStocks("Apple")</code> auf</li>
     *   <li>Repository sucht in der lokalen Datenbank nach Aktien, die "Apple" im Namen oder Symbol enthalten</li>
     *   <li>Controller sendet gefundene Aktien als JSON zurück</li>
     * </ol>
     *
     * <h3>Annotationen erklärt:</h3>
     * <ul>
     *   <li><b>@GetMapping("/search"):</b>
     *       Reagiert auf GET-Requests an <code>/api/stocks/search</code></li>
     *   <li><b>@RequestParam String query:</b>
     *       Holt den Wert des Query-Parameters <code>query</code> aus der URL.
     *       Bei <code>/search?query=Tesla</code> wäre <code>query = "Tesla"</code></li>
     * </ul>
     *
     * <h3>Datenquelle:</h3>
     * <p><b>Wichtig:</b> Dieser Endpoint durchsucht nur die lokale Datenbank, NICHT die FMP API.
     * Für eine vollständige Suche über alle verfügbaren Aktien könnte ein zusätzlicher
     * Endpoint implementiert werden, der die FMP Search API nutzt:</p>
     * <pre>
     * GET https://financialmodelingprep.com/api/v3/search?query={query}&apikey={API_KEY}
     * </pre>
     *
     * <h3>Query-Parameter vs. Path-Variable:</h3>
     * <ul>
     *   <li><b>Path-Variable:</b> <code>/stocks/AAPL</code> → Pfad-Teil der URL</li>
     *   <li><b>Query-Parameter:</b> <code>/search?query=Apple</code> → Nach dem ? in der URL</li>
     * </ul>
     *
     * <h3>Verwendungszweck:</h3>
     * <p>Dieser Endpoint wird für Suchfunktionen verwendet, z.B. wenn ein Benutzer
     * in einem Suchfeld tippt und passende Aktien aus der Watchlist/Datenbank
     * angezeigt werden sollen.</p>
     *
     * <h3>Beispiel-Request und Response:</h3>
     * <pre>
     * Request:  GET /api/stocks/search?query=App
     *
     * Response: HTTP 200 OK
     * [
     *   {
     *     "id": 1,
     *     "symbol": "AAPL",
     *     "name": "Apple Inc.",
     *     "exchange": "NASDAQ"
     *   }
     * ]
     * </pre>
     *
     * @param query Der Suchbegriff (wird in Symbol und Name der Aktien gesucht)
     * @return ResponseEntity mit Liste der gefundenen Stock-Objekte aus der lokalen DB und HTTP-Status 200 (OK)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String query) {
        // Repository-Aufruf: Sucht nach Aktien in der lokalen Datenbank, die zum Suchbegriff passen
        List<Stock> results = stockRepository.searchStocks(query);

        // Gibt die gefundenen Ergebnisse zurück
        return ResponseEntity.ok(results);
    }
}
