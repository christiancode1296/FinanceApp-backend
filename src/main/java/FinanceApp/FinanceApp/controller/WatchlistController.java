package FinanceApp.FinanceApp.controller;

import FinanceApp.FinanceApp.entity.WatchlistItem;
import FinanceApp.FinanceApp.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistRepository repository;

    @GetMapping("/{userId}")
    public List<WatchlistItem> getWatchlist(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping("/{userId}")
    public WatchlistItem addToWatchlist(@PathVariable String userId, @RequestBody WatchlistItem item) {
        item.setUserId(userId);
        return repository.save(item);
    }

    @DeleteMapping("/{userId}/{symbol}")
    @Transactional
    public void removeFromWatchlist(@PathVariable String userId, @PathVariable String symbol) {
        repository.deleteByUserIdAndSymbol(userId, symbol);
    }
}
