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
public class ForeignFlowChartCacheId implements Serializable {

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(name = "entity_code", nullable = false, length = 50)
    private String entityCode;

    @Column(name = "date_sk", nullable = false)
    private Integer dateSk;

    @Column(name = "timeframe", nullable = false, length = 20)
    private String timeframe;
}
