package com.mobile.backendjava.dm.dto.anomaly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAnomalyDTO {

    private Long id;
    private String symbol;
    private String sectorGroup;
    private String groupName;
    private LocalDate predictionDate;
    private Double score;
    private Double p95;
    private Double p98;
    private Double scoreOverP95;
    private Double scoreOverP98;
    private Double baselineScore;
    private Integer baselineWindows;
    private Double scoreRatioVsBaseline;
    private Integer anomalyCode;
    private String anomalyLevel;
    private String relativeLevel;
    private String finalDecision;
}
