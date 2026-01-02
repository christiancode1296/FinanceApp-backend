package FinanceApp.FinanceApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "historical_data")
public class HistoricalData {

    // Getter und Setter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hist_seq")
    @SequenceGenerator(name = "hist_seq", sequenceName = "historical_data_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private LocalDate date;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Long volume;

    // Constructor
    public HistoricalData() {
    }

    public HistoricalData(String symbol, LocalDate date, BigDecimal price, Long volume) {
        this.symbol = symbol;
        this.date = date;
        this.price = price;
        this.volume = volume;
    }

}
