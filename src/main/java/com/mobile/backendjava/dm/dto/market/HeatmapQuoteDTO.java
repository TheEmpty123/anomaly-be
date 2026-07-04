package com.mobile.backendjava.dm.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapQuoteDTO {
    private String symbol;
    private BigDecimal price;
    private BigDecimal refPrice;
    private BigDecimal pctChange;
    private Long volume;
    private BigDecimal txnValue;
    private BigDecimal marketCap;
    private String status;
    private String sector;
    private String industry;
    private String exchange;
    private String lastUpdated;
}
