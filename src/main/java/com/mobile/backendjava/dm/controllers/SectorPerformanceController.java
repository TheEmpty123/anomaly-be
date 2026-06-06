package com.mobile.backendjava.dm.controllers;

import com.mobile.backendjava.dm.service.SectorPerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.jpa.base-path}")
public class SectorPerformanceController {

    private final SectorPerformanceService sectorPerformanceService;

    public SectorPerformanceController(SectorPerformanceService sectorPerformanceService) {
        this.sectorPerformanceService = sectorPerformanceService;
    }

    @GetMapping("/sector-performance")
    public ResponseEntity<List<Map<String, Object>>> latestSectorPerformance(
            @RequestParam String timeframe,
            @RequestParam(required = false) String sectorCode
    ) {
        if (timeframe == null || timeframe.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sectorPerformanceService.getLatest(timeframe.trim(), sectorCode));
    }
}
