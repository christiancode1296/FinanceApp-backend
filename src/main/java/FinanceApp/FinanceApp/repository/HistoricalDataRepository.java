package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository für den Zugriff auf historische Aktiendaten.
 * <p>
 * Dieses Interface erweitert {@link JpaRepository} und bietet automatisch generierte
 * CRUD-Operationen sowie custom Query-Methoden für die {@link HistoricalData}-Entity.
 * Spring Data JPA generiert die Implementierung zur Laufzeit basierend auf den Methodennamen.
 * </p>
 * <p>
 * Verwendete Namenskonventionen:
 * <ul>
 *   <li>{@code findBy...} - SELECT-Query</li>
 *   <li>{@code existsBy...} - EXISTS-Query</li>
 *   <li>{@code OrderBy...Desc} - Sortierung absteigend</li>
 *   <li>{@code And} - WHERE-Bedingungen verknüpfen</li>
 * </ul>
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2025-01-02
 * @see HistoricalData
 * @see JpaRepository
 */
@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, Long> {

    /**
     * Findet einen historischen Datensatz für ein bestimmtes Aktiensymbol an einem bestimmten Datum.
     * <p>
     * Generierte SQL-Query: {@code SELECT * FROM historical_data WHERE symbol = ? AND date = ?}
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     * @param date   das Datum der gesuchten Daten
     * @return ein {@link Optional} mit dem gefundenen Datensatz oder leer, wenn nicht vorhanden
     */
    Optional<HistoricalData> findBySymbolAndDate(String symbol, LocalDate date);

    /**
     * Findet den neuesten (jüngsten) historischen Datensatz für ein bestimmtes Aktiensymbol.
     * <p>
     * Generierte SQL-Query: {@code SELECT * FROM historical_data WHERE symbol = ? ORDER BY date DESC LIMIT 1}
     * </p>
     * <p>
     * Wird verwendet, um zu prüfen, wann die letzten Daten für ein Symbol gespeichert wurden.
     * </p>
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     * @return ein {@link Optional} mit dem neuesten Datensatz oder leer, wenn keine Daten vorhanden
     */
    Optional<HistoricalData> findFirstBySymbolOrderByDateDesc(String symbol);

    /**
     * Findet alle historischen Datensätze für ein bestimmtes Aktiensymbol,
     * sortiert nach Datum in absteigender Reihenfolge (neueste zuerst).
     * <p>
     * Generierte SQL-Query: {@code SELECT * FROM historical_data WHERE symbol = ? ORDER BY date DESC}
     * </p>
     * <p>
     * Wird verwendet, um die vollständige Kurshistorie für die Anzeige im Frontend abzurufen.
     * </p>
     *
     * @param sanitizedSymbol das bereinigte Aktiensymbol (z.B. "AAPL")
     * @return eine Liste aller historischen Datensätze, sortiert nach Datum (neueste zuerst)
     */
    List<HistoricalData> findAllBySymbolOrderByDateDesc(String sanitizedSymbol);

    /**
     * Prüft, ob für ein bestimmtes Aktiensymbol bereits historische Daten in der Datenbank existieren.
     * <p>
     * Generierte SQL-Query: {@code SELECT EXISTS(SELECT 1 FROM historical_data WHERE symbol = ?)}
     * </p>
     * <p>
     * Wird verwendet, um zu validieren, ob bereits Daten für ein Symbol vorhanden sind,
     * bevor neue Daten abgerufen werden.
     * </p>
     *
     * @param sanitizedSymbol das bereinigte Aktiensymbol (z.B. "AAPL")
     * @return {@code true}, wenn Daten existieren, {@code false} sonst
     */
    boolean existsBySymbol(String sanitizedSymbol);
}
