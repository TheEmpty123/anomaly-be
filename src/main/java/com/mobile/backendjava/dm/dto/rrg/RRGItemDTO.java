package com.mobile.backendjava.dm.dto.rrg;

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
public class RRGItemDTO {
    private String sectorCode;
    private Integer dateSk;
    private BigDecimal rs;
    private BigDecimal rm;
    private String phase;
    private Integer stockCount;
    private Integer totalStocks;
    private String sectorName;
    private String sectorNameEn;
    private String blockType;
    private String topStocksByCap; // JSON string
    private String benchmark;
    private LocalDateTime ingestionTime;
    private String regime;
}
