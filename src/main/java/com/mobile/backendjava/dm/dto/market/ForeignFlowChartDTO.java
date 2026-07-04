package com.mobile.backendjava.dm.dto.market;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForeignFlowChartDTO {
    private String entityType;
    private String entityCode;
    private Integer dateSk;
    private String timeframe;
    private BigDecimal buyVal;
    private BigDecimal sellVal;
    private BigDecimal netVal;
    private BigDecimal cumulativeNetVal;
    private BigDecimal close;
    private BigDecimal priceIndex100;
    private String benchmarkCode;
    private BigDecimal benchmarkClose;
    private BigDecimal benchmarkIndex100;
    private LocalDateTime ingestionTime;
}
