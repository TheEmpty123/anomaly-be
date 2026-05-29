package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SectorPerformanceId implements Serializable {
    @Column(name = "sector_code", nullable = false, length = 50)
    private String sectorCode;

    @Column(name = "timeframe", nullable = false, length = 20)
    private String timeframe;
}
