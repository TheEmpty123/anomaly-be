package com.mobile.backendjava.dm.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeignFlowChartDTO {
    private String entityType;
    private String entityCode;
    private Integer dateSk;
    private String timeframe;
    private BigDecimal buyVal;
    private BigDecimal sellVal;
    private BigDecimal netVal;
    private BigDecimal cumulativeNetVal;
    private Long buyVol;
    private Long sellVol;
    private Long netVol;
    private Long cumulativeNetVol;
    private BigDecimal close;
    private BigDecimal priceIndex100;
    private String benchmarkCode;
    private BigDecimal benchmarkClose;
    private BigDecimal benchmarkIndex100;
    private LocalDateTime ingestionTime;
}
