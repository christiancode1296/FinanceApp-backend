package FinanceApp.FinanceApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA-Entity zur Speicherung historischer Aktiendaten.
 * <p>
 * Diese Klasse bildet die Tabelle "historical_data" in der PostgreSQL-Datenbank ab
 * und enthält historische Kursinformationen für Aktien, einschließlich Symbol,
 * Datum, Schlusskurs und Handelsvolumen.
 * </p>
 * <p>
 * Die ID wird automatisch über eine PostgreSQL-Sequence generiert,
 * wobei aus Performance-Gründen 50 IDs auf einmal allokiert werden.
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2025-01-02
 */
@Setter
@Getter
@Entity
@Table(name = "historical_data")
public class HistoricalData {

    /**
     * Primärschlüssel der historischen Daten.
     * <p>
     * Wird automatisch über die Sequence "historical_data_seq" generiert.
     * Die Allocation Size von 50 optimiert die Performance bei Batch-Inserts.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hist_seq")
    @SequenceGenerator(name = "hist_seq", sequenceName = "historical_data_seq", allocationSize = 50)
    private Long id;

    /**
     * Aktiensymbol (Ticker).
     * <p>
     * Beispiel: "AAPL" für Apple Inc., "MSFT" für Microsoft Corporation
     * </p>
     */
    @Column(nullable = false)
    private String symbol;

    /**
     * Datum der historischen Kursdaten.
     * <p>
     * Repräsentiert den Handelstag, für den die Kursinformationen gespeichert werden.
     * </p>
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Schlusskurs der Aktie am angegebenen Datum.
     * <p>
     * Gespeichert mit einer Präzision von 10 Stellen und 2 Nachkommastellen (z.B. 12345678.90).
     * </p>
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * Handelsvolumen am angegebenen Datum.
     * <p>
     * Anzahl der gehandelten Aktien während des Handelstages.
     * </p>
     */
    @Column(nullable = false)
    private Long volume;

    /**
     * Standard-Konstruktor für JPA.
     * <p>
     * Wird von JPA/Hibernate zur Instanziierung verwendet.
     * </p>
     */
    public HistoricalData() {
    }

    /**
     * Konstruktor zur Erstellung eines neuen HistoricalData-Objekts.
     *
     * @param symbol das Aktiensymbol (z.B. "AAPL")
     * @param date   das Datum der historischen Daten
     * @param price  der Schlusskurs der Aktie
     * @param volume das Handelsvolumen
     */
    public HistoricalData(String symbol, LocalDate date, BigDecimal price, Long volume) {
        this.symbol = symbol;
        this.date = date;
        this.price = price;
        this.volume = volume;
    }
}
