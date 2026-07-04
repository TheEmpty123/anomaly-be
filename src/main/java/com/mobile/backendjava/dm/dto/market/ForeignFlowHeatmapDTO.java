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
public class ForeignFlowHeatmapDTO {
    private Integer dateSk;
    private String timeframe;
    private String symbol;
    private Integer symbolSk;
    private BigDecimal netVal;
    private BigDecimal cumulativeNetVal;
    private Long netVol;
    private Long cumulativeNetVol;
    private BigDecimal close;
    private BigDecimal pctChange;
    private Long volume;
    private BigDecimal value;
    private BigDecimal marketCap;
    private BigDecimal marketWeight;
    private Integer rankNetBuy;
    private Integer rankNetSell;
    private BigDecimal intensity;
    private String direction;
    private LocalDateTime ingestionTime;
}
