package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.OhlcvDTO;
import com.mobile.backendjava.dm.service.OhlcvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/ohlcv")
public class StellarOhlcvController {

    private final OhlcvService ohlcvService;

    public StellarOhlcvController(OhlcvService ohlcvService) {
        this.ohlcvService = ohlcvService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<List<OhlcvDTO>> getBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer fromDateSk,
            @RequestParam(required = false) Integer toDateSk,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        try {
            return ResponseEntity.ok(ohlcvService.getBySymbol(symbol, timeframe, fromDateSk, toDateSk, limit, order));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OhlcvDTO>> getByDate(
            @RequestParam Integer dateSk,
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ohlcvService.getByDate(dateSk, timeframe, limit));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<OhlcvDTO>> getLatest(
            @RequestParam(required = false, defaultValue = "1d") String timeframe,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ohlcvService.getLatest(timeframe, limit));
    }
}
