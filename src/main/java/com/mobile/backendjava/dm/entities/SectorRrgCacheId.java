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
public class SectorRrgCacheId implements Serializable {
    @Column(name = "sector_code", nullable = false, length = 50)
    private String sectorCode;

    @Column(name = "date_sk", nullable = false)
    private Integer dateSk;

    @Column(name = "regime", nullable = false, length = 20)
    private String regime;

    @Column(name = "benchmark", length = 20)
    private String benchmark;
}
