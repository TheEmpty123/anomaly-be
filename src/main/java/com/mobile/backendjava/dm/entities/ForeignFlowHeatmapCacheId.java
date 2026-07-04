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
public class ForeignFlowHeatmapCacheId implements Serializable {

    @Column(name = "date_sk", nullable = false)
    private Integer dateSk;

    @Column(name = "timeframe", nullable = false, length = 20)
    private String timeframe;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;
}
