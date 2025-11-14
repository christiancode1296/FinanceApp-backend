package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE UPPER(s.name) LIKE UPPER(CONCAT('%', :query, '%')) OR UPPER(s.symbol) LIKE UPPER(CONCAT('%', :query, '%')) ORDER BY s.symbol ASC")
    List<Stock> searchStocks(String query);
}