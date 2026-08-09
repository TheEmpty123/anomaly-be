package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.IndexImpactHistoryPointDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactSnapshotDTO;
import com.mobile.backendjava.dm.service.market.MarketRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/index-impact")
@Slf4j
public class StellarIndexImpactController {

    private final MarketRedisService marketRedisService;

    public StellarIndexImpactController(MarketRedisService marketRedisService) {
        this.marketRedisService = marketRedisService;
    }

    @GetMapping("/{indexCode}/latest")
    public ResponseEntity<IndexImpactSnapshotDTO> latest(@PathVariable String indexCode) {
        log.info("event=controller.request action=index-impact.latest indexCode={}", indexCode);
        try {
            return marketRedisService.getLatestIndexImpact(indexCode)
                    .map(snapshot -> {
                        log.info("event=controller.response action=index-impact.latest status=200 indexCode={} timestamp={}",
                                indexCode, snapshot.getTimestamp());
                        return ResponseEntity.ok(snapshot);
                    })
                    .orElseGet(() -> {
                        log.info("event=controller.response action=index-impact.latest status=204 indexCode={}", indexCode);
                        return ResponseEntity.noContent().build();
                    });
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=index-impact.latest status=400 indexCode={} errorMessage={}",
                    indexCode, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{indexCode}/history")
    public ResponseEntity<List<IndexImpactHistoryPointDTO>> history(
            @PathVariable String indexCode,
            @RequestParam(required = false) String date) {
        log.info("event=controller.request action=index-impact.history indexCode={} date={}", indexCode, date);
        try {
            List<IndexImpactHistoryPointDTO> history = marketRedisService.getIndexImpactHistory(indexCode, date);
            log.info("event=controller.response action=index-impact.history status=200 indexCode={} date={} resultCount={}",
                    indexCode, date, history.size());
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=index-impact.history status=400 indexCode={} date={} errorMessage={}",
                    indexCode, date, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
