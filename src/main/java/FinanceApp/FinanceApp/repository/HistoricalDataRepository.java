package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, Long> {
    Optional<HistoricalData> findBySymbolAndDate(String symbol, LocalDate date);

    Optional<HistoricalData> findFirstBySymbolOrderByDateDesc(String symbol);

    List<HistoricalData> findAllBySymbolOrderByDateDesc(String sanitizedSymbol);

    boolean existsBySymbol(String sanitizedSymbol);
}
