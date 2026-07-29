package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.rrg.RRGItemDTO;
import com.mobile.backendjava.dm.dto.rrg.RRGRequestDTO;
import com.mobile.backendjava.dm.dto.rrg.RRGResponseDTO;
import com.mobile.backendjava.dm.model.Regime;
import com.mobile.backendjava.dm.service.RRGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.stellar.base-path}")
@Slf4j
public class StellarRRGController {

    private final RRGService rrgService;

    public StellarRRGController(@org.springframework.beans.factory.annotation.Qualifier("RRGServiceImpl") RRGService rrgService) {
        this.rrgService = rrgService;
    }

    @GetMapping("/rrg")
    public ResponseEntity<RRGResponseDTO> getRRG(@ModelAttribute RRGRequestDTO request) {
        log.info("event=controller.request action=rrg.get regime={} benchmark={} dateSk={}",
                request == null ? null : request.getRegime(), request == null ? null : request.getBenchmark(),
                request == null ? null : request.getDateSk());
        if (request == null || request.getRegime() == null || request.getRegime().isBlank()) {
            log.warn("event=controller.response action=rrg.get status=400 reason=missing-regime");
            return ResponseEntity.badRequest().build();
        }
        Regime regime = Regime.fromString(request.getRegime());
        if (regime == null) {
            log.warn("event=controller.response action=rrg.get status=400 reason=invalid-regime regime={}", request.getRegime());
            return ResponseEntity.badRequest().build();
        }
        String benchmark = (request.getBenchmark() == null || request.getBenchmark().isBlank())
                ? "VNINDEX" : request.getBenchmark().trim();

        List<Map<String, Object>> rows = rrgService.getSectorRRG(regime.name(), benchmark, request.getDateSk());
        List<RRGItemDTO> items = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            RRGItemDTO dto = RRGItemDTO.builder()
                    .sectorCode((String) m.get("sector_code"))
                    .dateSk((Integer) m.get("date_sk"))
                    .rs((BigDecimal) m.get("rs"))
                    .rm((BigDecimal) m.get("rm"))
                    .phase((String) m.get("phase"))
                    .stockCount((Integer) m.get("stock_count"))
                    .totalStocks((Integer) m.get("total_stocks"))
                    .sectorName((String) m.get("sector_name"))
                    .sectorNameEn((String) m.get("sector_name_en"))
                    .blockType((String) m.get("block_type"))
                    .topStocksByCap((String) m.get("top_stocks_by_cap"))
                    .benchmark((String) m.get("benchmark"))
                    .ingestionTime((LocalDateTime) m.get("ingestion_time"))
                    .regime((String) m.get("regime"))
                    .totalVolume((Long) m.get("total_volume"))
                    .totalValue((Long) m.get("total_value"))
                    .totalMarketCap((Long) m.get("total_market_cap"))
                    .avgMarketCap((BigDecimal) m.get("avg_market_cap"))
                    .liquidityScore((BigDecimal) m.get("liquidity_score"))
                    .totalFreefloatMarketCap((BigDecimal) m.get("total_freefloat_market_cap"))
                    .avgMarketWeight((BigDecimal) m.get("avg_market_weight"))
                    .build();
            items.add(dto);
        }
        if (items.isEmpty()) {
            log.info("event=controller.response action=rrg.get status=204 resultCount=0");
            return ResponseEntity.noContent().build();
        }
        log.info("event=controller.response action=rrg.get status=200 resultCount={} regime={} benchmark={}",
                items.size(), regime, benchmark);
        return ResponseEntity.ok(RRGResponseDTO.builder().items(items).build());
    }
}
