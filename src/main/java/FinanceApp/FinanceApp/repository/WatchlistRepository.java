package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.domain.*;
import FinanceApp.FinanceApp.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Spring Data JPA Repository für den Zugriff auf Watchlists (Beobachtungslisten).
 * <p>
 * Dieses Interface erweitert {@link JpaRepository} und bietet automatisch generierte
 * CRUD-Operationen für die {@link Watchlist}-Entity. Im Gegensatz zu anderen Repositories
 * verwendet dieses Interface {@link UUID} als Primärschlüssel-Typ.
 * </p>
 * <p>
 * Watchlists ermöglichen es Nutzern, Aktien zu einer Beobachtungsliste hinzuzufügen,
 * um deren Kursentwicklung zu verfolgen.
 * </p>
 * <p>
 * Aktuell werden nur die Standard-CRUD-Operationen benötigt. Custom Queries können
 * bei Bedarf ergänzt werden (z.B. {@code findByUserId()}, {@code findByNameContaining()}).
 * </p>
 *
 * @author christiancode1296
 * @version 1.0
 * @since 2025-01-02
 * @see Watchlist
 * @see JpaRepository
 */
@Repository
public interface WatchlistRepository extends JpaRepository<FinanceApp.FinanceApp.entity.WatchlistItem, Long> {
    List<WatchlistItem> findByUserId(String userId);
    void deleteByUserIdAndSymbol(String userId, String symbol);
}

