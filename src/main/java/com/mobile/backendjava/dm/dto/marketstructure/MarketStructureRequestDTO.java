package com.mobile.backendjava.dm.dto.marketstructure;

import com.mobile.backendjava.dm.model.Timeframe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketStructureRequestDTO {
    private String timeframe; // 1M,3M,6M,1Y
    private String benchmark; // optional, defaults to VNINDEX

    public Timeframe timeframeEnum() {
        return Timeframe.fromString(timeframe);
    }
}
