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
public class IndexValuationDailyId implements Serializable {

    @Column(name = "symbol_sk", nullable = false)
    private Integer symbolSk;

    @Column(name = "date_sk", nullable = false)
    private Integer dateSk;
}
