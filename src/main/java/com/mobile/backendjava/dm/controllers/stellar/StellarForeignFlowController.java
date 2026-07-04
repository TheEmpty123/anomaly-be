package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.ForeignFlowChartDTO;
import com.mobile.backendjava.dm.dto.market.ForeignFlowHeatmapDTO;
import com.mobile.backendjava.dm.service.ForeignFlowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/foreign-flow")
public class StellarForeignFlowController {

    private final ForeignFlowService foreignFlowService;

    public StellarForeignFlowController(ForeignFlowService foreignFlowService) {
        this.foreignFlowService = foreignFlowService;
    }

    @GetMapping("/chart")
    public ResponseEntity<List<ForeignFlowChartDTO>> chart(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityCode,
            @RequestParam(required = false) String timeframe,
            @RequestParam(required = false) Integer fromDateSk,
            @RequestParam(required = false) Integer toDateSk,
            @RequestParam(required = false) Integer limit) {
        try {
            return ResponseEntity.ok(foreignFlowService.getChart(entityType, entityCode, timeframe, fromDateSk, toDateSk, limit));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<ForeignFlowHeatmapDTO>> heatmap(
            @RequestParam(required = false) Integer dateSk,
            @RequestParam(required = false) String timeframe,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String direction) {
        try {
            return ResponseEntity.ok(foreignFlowService.getHeatmap(dateSk, timeframe, limit, direction));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
