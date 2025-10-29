package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.domain.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, UUID> {
}
