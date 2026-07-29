package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.anomaly.StockAnomalyDTO;
import com.mobile.backendjava.dm.service.StockAnomalyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/stock-anomalies")
public class StellarStockAnomalyController {

    private final StockAnomalyService stockAnomalyService;

    public StellarStockAnomalyController(StockAnomalyService stockAnomalyService) {
        this.stockAnomalyService = stockAnomalyService;
    }

    @GetMapping
    public ResponseEntity<List<StockAnomalyDTO>> getStockAnomalies(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(stockAnomalyService.getByPredictionDate(date));
    }
}
