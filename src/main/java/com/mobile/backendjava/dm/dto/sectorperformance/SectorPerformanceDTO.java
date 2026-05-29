package com.mobile.backendjava.dm.dto.sectorperformance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectorPerformanceDTO {
    private String sectorCode;
    private String blockType;
    private String timeframe;
    private String chartData; // JSON string
    private LocalDateTime ingestionTime;
}
