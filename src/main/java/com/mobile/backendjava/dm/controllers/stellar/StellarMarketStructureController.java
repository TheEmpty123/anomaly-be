package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.marketstructure.MarketStructureDTO;
import com.mobile.backendjava.dm.dto.marketstructure.MarketStructureRequestDTO;
import com.mobile.backendjava.dm.model.Timeframe;
import com.mobile.backendjava.dm.service.MarketStructureService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("${api.stellar.base-path}")
public class StellarMarketStructureController {

    private final MarketStructureService marketStructureService;

    public StellarMarketStructureController(@Qualifier("marketStructureServiceImpl") MarketStructureService marketStructureService) {
        this.marketStructureService = marketStructureService;
    }

    @GetMapping("/market-structure")
    public ResponseEntity<MarketStructureDTO> getMarketStructure(@ModelAttribute MarketStructureRequestDTO request) {
        if (request == null || request.getTimeframe() == null || request.getTimeframe().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Timeframe tf = Timeframe.fromString(request.getTimeframe());
        if (tf == null) {
            return ResponseEntity.badRequest().build();
        }
        String benchmark = (request.getBenchmark() == null || request.getBenchmark().isBlank())
                ? "VNINDEX" : request.getBenchmark().trim();
        Map<String, Object> m = marketStructureService.getLatest(tf.getCode(), benchmark);
        if (m == null || m.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        MarketStructureDTO dto = MarketStructureDTO.builder()
                .dateSk((Integer) m.get("date_sk"))
                .timeframe((String) m.get("timeframe"))
                .benchmark((String) m.get("benchmark"))
                .marketStructureCode((String) m.get("market_structure_code"))
                .marketStructureLabel((String) m.get("market_structure_label"))
                .coreSectors((String) m.get("core_sectors"))
                .coreBlocks((String) m.get("core_blocks"))
                .topEcosystemCode((String) m.get("top_ecosystem_code"))
                .topEcosystemName((String) m.get("top_ecosystem_name"))
                .sectorRankings((String) m.get("sector_rankings"))
                .ecosystemRankings((String) m.get("ecosystem_rankings"))
                .ingestionTime((LocalDateTime) m.get("ingestion_time"))
                .build();
        return ResponseEntity.ok(dto);
    }
}
