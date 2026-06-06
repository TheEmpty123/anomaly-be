package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.rrg.RRGItemDTO;
import com.mobile.backendjava.dm.dto.rrg.RRGRequestDTO;
import com.mobile.backendjava.dm.dto.rrg.RRGResponseDTO;
import com.mobile.backendjava.dm.model.Regime;
import com.mobile.backendjava.dm.service.RRGService;
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
public class StellarRRGController {

    private final RRGService rrgService;

    public StellarRRGController(@org.springframework.beans.factory.annotation.Qualifier("RRGServiceImpl") RRGService rrgService) {
        this.rrgService = rrgService;
    }

    @GetMapping("/rrg")
    public ResponseEntity<RRGResponseDTO> getRRG(@ModelAttribute RRGRequestDTO request) {
        if (request == null || request.getRegime() == null || request.getRegime().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Regime regime = Regime.fromString(request.getRegime());
        if (regime == null) {
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
                    .build();
            items.add(dto);
        }
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RRGResponseDTO.builder().items(items).build());
    }
}
