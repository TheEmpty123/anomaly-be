package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.service.market.MarketRedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.stellar.base-path}/market/breadth")
public class StellarMarketBreadthController {

    private final MarketRedisService marketRedisService;

    public StellarMarketBreadthController(MarketRedisService marketRedisService) {
        this.marketRedisService = marketRedisService;
    }

    @GetMapping
    public ResponseEntity<Object> current() {
        Object breadth = marketRedisService.getCurrentBreadth();
        return breadth == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(breadth);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> history(@RequestParam(required = false) String date) {
        Object breadth = marketRedisService.getBreadthHistory(date);
        return breadth == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(breadth);
    }
}
