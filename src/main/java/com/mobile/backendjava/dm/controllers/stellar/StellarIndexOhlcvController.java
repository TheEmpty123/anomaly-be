package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.IndexOhlcvDTO;
import com.mobile.backendjava.dm.service.IndexOhlcvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/index-ohlcv")
public class StellarIndexOhlcvController {

    private final IndexOhlcvService indexOhlcvService;

    public StellarIndexOhlcvController(IndexOhlcvService indexOhlcvService) {
        this.indexOhlcvService = indexOhlcvService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<List<IndexOhlcvDTO>> getBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer fromDateSk,
            @RequestParam(required = false) Integer toDateSk,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        try {
            return ResponseEntity.ok(indexOhlcvService.getBySymbol(symbol, timeframe, fromDateSk, toDateSk, limit, order));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<IndexOhlcvDTO>> getByDate(
            @RequestParam Integer dateSk,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(indexOhlcvService.getByDate(dateSk, timeframe, limit));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<IndexOhlcvDTO>> getLatest(
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(indexOhlcvService.getLatest(timeframe, limit));
    }
}
