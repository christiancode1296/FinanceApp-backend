package FinanceApp.FinanceApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "watchlist")
public class Watchlist {

    @Id
    @GeneratedValue
    private UUID id; //Primary

    @ManyToOne(fetch = FetchType.LAZY) // watchlisten zu einem user, fetch = lädt user daten nur bei Bedarf
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile; // FK to UserProfile

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistItem> items = new ArrayList<>();
}
