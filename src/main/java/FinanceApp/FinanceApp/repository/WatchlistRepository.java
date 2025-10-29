package FinanceApp.FinanceApp.repository;

import FinanceApp.FinanceApp.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {}
