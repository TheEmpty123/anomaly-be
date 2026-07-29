package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.service.market.MarketRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.stellar.base-path}/market/breadth")
@Slf4j
public class StellarMarketBreadthController {

    private final MarketRedisService marketRedisService;

    public StellarMarketBreadthController(MarketRedisService marketRedisService) {
        this.marketRedisService = marketRedisService;
    }

    @GetMapping
    public ResponseEntity<Object> current() {
        log.info("event=controller.request action=market-breadth.current");
        Object breadth = marketRedisService.getCurrentBreadth();
        log.info("event=controller.response action=market-breadth.current status={} resultType={}",
                breadth == null ? 204 : 200, breadth == null ? "null" : breadth.getClass().getSimpleName());
        return breadth == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(breadth);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> history(@RequestParam(required = false) String date) {
        log.info("event=controller.request action=market-breadth.history date={}", date);
        Object breadth = marketRedisService.getBreadthHistory(date);
        log.info("event=controller.response action=market-breadth.history status={} resultType={}",
                breadth == null ? 204 : 200, breadth == null ? "null" : breadth.getClass().getSimpleName());
        return breadth == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(breadth);
    }
}
