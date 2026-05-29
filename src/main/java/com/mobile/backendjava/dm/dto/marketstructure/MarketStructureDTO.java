package com.mobile.backendjava.dm.dto.marketstructure;

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
    private String coreSectors; // JSON string
    private String coreBlocks;  // JSON string
    private String topEcosystemCode;
    private String topEcosystemName;
    private String sectorRankings;    // JSON string
    private String ecosystemRankings; // JSON string
    private LocalDateTime ingestionTime;
}
