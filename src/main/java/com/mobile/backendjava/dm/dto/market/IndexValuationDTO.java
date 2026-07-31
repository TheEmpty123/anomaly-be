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
public class IndexValuationDTO {
    private String symbol;
    private LocalDate date;
    private BigDecimal indexClose;
    private BigDecimal pe;
    private BigDecimal pb;
}
