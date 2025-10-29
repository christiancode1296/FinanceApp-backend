package FinanceApp.FinanceApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "quoteCache", uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "type"})
)

public class QuoteCache {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private QuoteType type;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime validUntil;
}
