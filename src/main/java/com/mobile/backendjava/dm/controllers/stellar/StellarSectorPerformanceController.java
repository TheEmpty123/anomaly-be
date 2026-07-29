package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.sectorperformance.SectorPerformanceDTO;
import com.mobile.backendjava.dm.dto.sectorperformance.SectorPerformanceRequestDTO;
import com.mobile.backendjava.dm.model.Timeframe;
import com.mobile.backendjava.dm.service.SectorPerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.stellar.base-path}")
@Slf4j
public class StellarSectorPerformanceController {

    private final SectorPerformanceService sectorPerformanceService;

    public StellarSectorPerformanceController(@Qualifier("sectorPerformanceServiceImpl") SectorPerformanceService sectorPerformanceService) {
        this.sectorPerformanceService = sectorPerformanceService;
    }

    @GetMapping("/sector-performance")
    public ResponseEntity<List<SectorPerformanceDTO>> getSectorPerformance(@ModelAttribute SectorPerformanceRequestDTO request) {
        log.info("event=controller.request action=sector-performance.get timeframe={} sectorCode={}",
                request == null ? null : request.getTimeframe(), request == null ? null : request.getSectorCode());
        if (request == null || request.getTimeframe() == null || request.getTimeframe().isBlank()) {
            log.warn("event=controller.response action=sector-performance.get status=400 reason=missing-timeframe");
            return ResponseEntity.badRequest().build();
        }
        Timeframe tf = Timeframe.fromString(request.getTimeframe());
        if (tf == null) {
            log.warn("event=controller.response action=sector-performance.get status=400 reason=invalid-timeframe timeframe={}", request.getTimeframe());
            return ResponseEntity.badRequest().build();
        }
        List<Map<String, Object>> rows = sectorPerformanceService.getLatest(tf.getCode(), request.getSectorCode());
        if (rows == null || rows.isEmpty()) {
            log.info("event=controller.response action=sector-performance.get status=204 resultCount=0");
            return ResponseEntity.noContent().build();
        }
        List<SectorPerformanceDTO> items = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            SectorPerformanceDTO dto = SectorPerformanceDTO.builder()
                    .sectorCode((String) m.get("sector_code"))
                    .blockType((String) m.get("block_type"))
                    .timeframe((String) m.get("timeframe"))
                    .chartData((String) m.get("chart_data"))
                    .ingestionTime((LocalDateTime) m.get("ingestion_time"))
                    .build();
            items.add(dto);
        }
        log.info("event=controller.response action=sector-performance.get status=200 resultCount={} timeframe={} sectorCode={}",
                items.size(), tf.getCode(), request.getSectorCode());
        return ResponseEntity.ok(items);
    }
}
