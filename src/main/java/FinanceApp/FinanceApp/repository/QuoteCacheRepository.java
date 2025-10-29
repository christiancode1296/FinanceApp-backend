package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.domain.QuoteCache;
import FinanceApp.FinanceApp.domain.QuoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuoteCacheRepository extends JpaRepository<QuoteCache, Long> {
    Optional<QuoteCache> findBySymbolAndType(String symbol, QuoteType type);
}
