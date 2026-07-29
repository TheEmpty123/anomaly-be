package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.IndexOhlcvDTO;
import com.mobile.backendjava.dm.service.IndexOhlcvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/index-ohlcv")
@Slf4j
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
        log.info("event=controller.request action=index-ohlcv.by-symbol symbol={} timeframe={} fromDateSk={} toDateSk={} limit={} order={}",
                symbol, timeframe, fromDateSk, toDateSk, limit, order);
        try {
            List<IndexOhlcvDTO> result = indexOhlcvService.getBySymbol(symbol, timeframe, fromDateSk, toDateSk, limit, order);
            log.info("event=controller.response action=index-ohlcv.by-symbol status=200 resultCount={}", result.size());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=index-ohlcv.by-symbol status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<IndexOhlcvDTO>> getByDate(
            @RequestParam Integer dateSk,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        log.info("event=controller.request action=index-ohlcv.by-date dateSk={} timeframe={} limit={}", dateSk, timeframe, limit);
        List<IndexOhlcvDTO> result = indexOhlcvService.getByDate(dateSk, timeframe, limit);
        log.info("event=controller.response action=index-ohlcv.by-date status=200 resultCount={}", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<IndexOhlcvDTO>> getLatest(
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        log.info("event=controller.request action=index-ohlcv.latest timeframe={} limit={}", timeframe, limit);
        List<IndexOhlcvDTO> result = indexOhlcvService.getLatest(timeframe, limit);
        log.info("event=controller.response action=index-ohlcv.latest status=200 resultCount={}", result.size());
        return ResponseEntity.ok(result);
    }
}
