package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.ForeignFlowChartDTO;
import com.mobile.backendjava.dm.dto.market.ForeignFlowHeatmapDTO;
import com.mobile.backendjava.dm.service.ForeignFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/foreign-flow")
@Slf4j
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
        log.info("event=controller.request action=foreign-flow.chart entityType={} entityCode={} timeframe={} fromDateSk={} toDateSk={} limit={}",
                entityType, entityCode, timeframe, fromDateSk, toDateSk, limit);
        try {
            List<ForeignFlowChartDTO> result = foreignFlowService.getChart(entityType, entityCode, timeframe, fromDateSk, toDateSk, limit);
            log.info("event=controller.response action=foreign-flow.chart status=200 resultCount={}", result.size());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=foreign-flow.chart status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<ForeignFlowHeatmapDTO>> heatmap(
            @RequestParam(required = false) Integer dateSk,
            @RequestParam(required = false) String timeframe,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String direction) {
        log.info("event=controller.request action=foreign-flow.heatmap dateSk={} timeframe={} limit={} direction={}",
                dateSk, timeframe, limit, direction);
        try {
            List<ForeignFlowHeatmapDTO> result = foreignFlowService.getHeatmap(dateSk, timeframe, limit, direction);
            log.info("event=controller.response action=foreign-flow.heatmap status=200 resultCount={}", result.size());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            log.warn("event=controller.response action=foreign-flow.heatmap status=400 errorMessage={}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
