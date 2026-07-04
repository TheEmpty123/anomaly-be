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
public class SymbolDTO {
    private Integer symbolSk;
    private String symbol;
    private Boolean isActive;
    private Long sharesOutstanding;
    private BigDecimal freefloat;
}
