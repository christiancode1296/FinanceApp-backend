package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository für den Zugriff auf Aktien-Stammdaten.
 * <p>
 * Dieses Interface erweitert {@link JpaRepository} und bietet automatisch generierte
 * CRUD-Operationen sowie custom Query-Methoden für die {@link Stock}-Entity.
 * </p>
 * <p>
 * Verwendet sowohl Spring Data Naming Conventions als auch custom JPQL-Queries
 * für komplexe Suchanfragen.
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2025-01-02
 * @see Stock
 * @see JpaRepository
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Findet eine Aktie anhand ihres eindeutigen Symbols.
     * <p>
     * Generierte SQL-Query: {@code SELECT * FROM stocks WHERE symbol = ?}
     * </p>
     * <p>
     * Wird verwendet, um zu prüfen, ob eine Aktie bereits in der Datenbank existiert,
     * bevor neue Daten hinzugefügt werden.
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     * @return ein {@link Optional} mit der gefundenen Aktie oder leer, wenn nicht vorhanden
     */
    Optional<Stock> findBySymbol(String symbol);

    /**
     * Sucht nach Aktien, deren Symbol oder Name den angegebenen Suchbegriff enthält.
     * <p>
     * Diese Methode führt eine Case-insensitive Wildcard-Suche in den Feldern
     * {@code symbol} und {@code name} durch.
     * </p>
     * <p>
     * Beispiel: {@code searchStocks("app")} findet:
     * <ul>
     *   <li>"AAPL" - "Apple Inc."</li>
     *   <li>"AMAT" - "Applied Materials"</li>
     * </ul>
     * </p>
     * <p>
     * Generierte SQL-Query:
     * {@code SELECT * FROM stocks WHERE LOWER(symbol) LIKE LOWER('%query%') OR LOWER(name) LIKE LOWER('%query%')}
     * </p>
     * <p>
     * Wird für die Autocomplete-Funktion und Suchleiste im Frontend verwendet.
     * </p>
     *
     * @param query der Suchbegriff (kann Teilstring sein)
     * @return eine Liste aller Aktien, die den Suchbegriff in Symbol oder Name enthalten
     */
    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Stock> searchStocks(String query);
}
