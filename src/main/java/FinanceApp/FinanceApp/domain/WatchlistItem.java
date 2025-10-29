package FinanceApp.FinanceApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "watchlist_item")
public class WatchlistItem {

    @Id
    @GeneratedValue
    private UUID id; //primary

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist; //fk

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column
    private String note;

    @Column(precision = 15, scale = 2)
    private BigDecimal targetPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
