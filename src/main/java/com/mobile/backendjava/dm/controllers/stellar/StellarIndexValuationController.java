package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.IndexValuationDTO;
import com.mobile.backendjava.dm.service.IndexValuationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/index-valuation")
@Slf4j
public class StellarIndexValuationController {

    private final IndexValuationService indexValuationService;

    public StellarIndexValuationController(IndexValuationService indexValuationService) {
        this.indexValuationService = indexValuationService;
    }

    @GetMapping("/{symbol}/historical")
    public ResponseEntity<List<IndexValuationDTO>> getHistorical(
            @PathVariable String symbol,
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("event=controller.request action=index-valuation.historical symbol={} startDate={} endDate={}",
                symbol, startDate, endDate);
        try {
            List<IndexValuationDTO> result = indexValuationService.getHistorical(symbol, startDate, endDate);
            log.info("event=controller.response action=index-valuation.historical status=200 resultCount={} result={}",
                    result.size(), result);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=index-valuation.historical status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{symbol}/latest")
    public ResponseEntity<IndexValuationDTO> getLatest(@PathVariable String symbol) {
        log.info("event=controller.request action=index-valuation.latest symbol={}", symbol);
        try {
            return indexValuationService.getLatest(symbol)
                    .map(result -> {
                        log.info("event=controller.response action=index-valuation.latest status=200 result={}", result);
                        return ResponseEntity.ok(result);
                    })
                    .orElseGet(() -> {
                        log.info("event=controller.response action=index-valuation.latest status=204 result=null");
                        return ResponseEntity.noContent().build();
                    });
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=index-valuation.latest status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
