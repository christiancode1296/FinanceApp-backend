package FinanceApp.FinanceApp.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA-Entity zur Speicherung von Aktien-Stammdaten.
 * <p>
 * Diese Klasse bildet die Tabelle "stocks" in der PostgreSQL-Datenbank ab
 * und enthält grundlegende Informationen über Aktien wie Symbol, Name und Börse.
 * </p>
 * <p>
 * Im Gegensatz zur {@link HistoricalData}-Entity, die historische Kursdaten speichert,
 * enthält diese Klasse nur die grundlegenden Metadaten einer Aktie.
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2025-01-02
 * @see HistoricalData
 */
@Setter
@Getter
@Entity
@Table(name = "stocks")
public class Stock {

    /**
     * Primärschlüssel der Aktie.
     * <p>
     * Wird automatisch von der Datenbank generiert (Auto-Increment).
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Eindeutiges Aktiensymbol (Ticker).
     * <p>
     * Beispiele: "AAPL" für Apple Inc., "MSFT" für Microsoft Corporation.
     * Muss eindeutig in der Datenbank sein und darf nicht NULL sein.
     * </p>
     */
    @Column(unique = true, nullable = false)
    private String symbol;

    /**
     * Vollständiger Name des Unternehmens.
     * <p>
     * Beispiel: "Apple Inc.", "Microsoft Corporation".
     * Darf nicht NULL sein.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * Börse, an der die Aktie gehandelt wird.
     * <p>
     * Beispiele: "NASDAQ", "NYSE", "XETRA".
     * Kann optional sein (NULL erlaubt).
     * </p>
     */
    private String exchange;

    /**
     * Standard-Konstruktor für JPA.
     * <p>
     * Wird von JPA/Hibernate zur Instanziierung verwendet.
     * </p>
     */
    public Stock() {
    }

    /**
     * Konstruktor zur Erstellung eines neuen Stock-Objekts.
     *
     * @param symbol   das eindeutige Aktiensymbol (z.B. "AAPL")
     * @param name     der vollständige Firmenname (z.B. "Apple Inc.")
     * @param exchange die Börse (z.B. "NASDAQ"), kann NULL sein
     */
    public Stock(String symbol, String name, String exchange) {
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
    }
}
