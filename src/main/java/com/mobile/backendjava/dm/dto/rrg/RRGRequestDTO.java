package com.mobile.backendjava.dm.dto.rrg;

import com.mobile.backendjava.dm.model.Regime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RRGRequestDTO {
    private String regime; // expect values matching Regime (case-insensitive)
    private String benchmark; // optional, default VNINDEX
    private Integer dateSk; // optional; when null, latest will be used

    public Regime regimeEnum() {
        return Regime.fromString(regime);
    }
}
