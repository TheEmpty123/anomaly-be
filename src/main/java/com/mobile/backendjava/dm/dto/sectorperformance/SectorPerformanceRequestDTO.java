package com.mobile.backendjava.dm.dto.sectorperformance;

import com.mobile.backendjava.dm.model.Timeframe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectorPerformanceRequestDTO {
    private String timeframe; // 1M,3M,6M,1Y
    private String sectorCode; // optional

    public Timeframe timeframeEnum() {
        return Timeframe.fromString(timeframe);
    }
}
