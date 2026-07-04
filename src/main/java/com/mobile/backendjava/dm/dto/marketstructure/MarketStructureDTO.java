package com.mobile.backendjava.dm.dto.marketstructure;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketStructureDTO {
    private Integer dateSk;
    private String timeframe;
    private String benchmark;
    private String marketStructureCode;
    private String marketStructureLabel;
    @JsonRawValue
    private String coreSectors; // JSON string
    @JsonRawValue
    private String coreBlocks;  // JSON string
    private String topEcosystemCode;
    private String topEcosystemName;
    @JsonRawValue
    private String sectorRankings;    // JSON string
    @JsonRawValue
    private String ecosystemRankings; // JSON string
    private LocalDateTime ingestionTime;
}
