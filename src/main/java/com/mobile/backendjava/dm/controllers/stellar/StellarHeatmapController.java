package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.HeatmapQuoteDTO;
import com.mobile.backendjava.dm.service.market.HeatmapSseService;
import com.mobile.backendjava.dm.service.market.MarketRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/heatmap")
@Slf4j
public class StellarHeatmapController {

    private final MarketRedisService marketRedisService;
    private final HeatmapSseService heatmapSseService;

    public StellarHeatmapController(MarketRedisService marketRedisService, HeatmapSseService heatmapSseService) {
        this.marketRedisService = marketRedisService;
        this.heatmapSseService = heatmapSseService;
    }

    @GetMapping("/snapshot")
    public ResponseEntity<List<HeatmapQuoteDTO>> snapshot() {
        log.info("event=controller.request action=heatmap.snapshot");
        List<HeatmapQuoteDTO> result = marketRedisService.getHeatmapSnapshot();
        log.info("event=controller.response action=heatmap.snapshot status=200 resultCount={}", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        log.info("event=controller.request action=heatmap.stream");
        log.info("event=controller.response action=heatmap.stream status=200 resultType=SseEmitter");
        return heatmapSseService.connect();
    }
}
