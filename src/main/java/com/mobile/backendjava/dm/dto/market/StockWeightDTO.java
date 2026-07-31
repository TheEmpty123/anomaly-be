package com.mobile.backendjava.dm.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockWeightDTO {
    private String symbol;
    private Integer symbolSk;
    private String companyName;
    private String sector;
    private Integer dateSk;
    private LocalDate fullDate;
    private String timeframe;
    private Integer timeSk;
    private BigDecimal close;
    private Long volume;
    private BigDecimal value;
    private BigDecimal marketCap;
    private BigDecimal marketWeight;
}
