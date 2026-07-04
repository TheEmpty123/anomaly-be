package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.anomaly.AnomalyDTO;
import com.mobile.backendjava.dm.service.AnomalyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}")
public class StellarAnomalyController {

    private final AnomalyService anomalyService;

    public StellarAnomalyController(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping("/anomalies")
    public ResponseEntity<List<AnomalyDTO>> getAnomalies() {
        return ResponseEntity.ok(anomalyService.getAnomalies());
    }
}
