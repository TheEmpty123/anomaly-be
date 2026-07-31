package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.StockWeightDTO;
import com.mobile.backendjava.dm.service.StockWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/stocks/weights")
@Slf4j
public class StellarStockWeightController {

    private final StockWeightService stockWeightService;

    public StellarStockWeightController(StockWeightService stockWeightService) {
        this.stockWeightService = stockWeightService;
    }

    @GetMapping
    public ResponseEntity<List<StockWeightDTO>> getWeights(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) Integer dateSk,
            @RequestParam(required = false) Integer fromDateSk,
            @RequestParam(required = false) Integer toDateSk,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        log.info("event=controller.request action=stocks.weights.list symbol={} sector={} dateSk={} fromDateSk={} toDateSk={} timeframe={} limit={}",
                symbol, sector, dateSk, fromDateSk, toDateSk, timeframe, limit);
        try {
            List<StockWeightDTO> result = stockWeightService.getWeights(symbol, sector, dateSk, fromDateSk, toDateSk, timeframe, limit);
            log.info("event=controller.response action=stocks.weights.list status=200 resultCount={}", result.size());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=stocks.weights.list status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
