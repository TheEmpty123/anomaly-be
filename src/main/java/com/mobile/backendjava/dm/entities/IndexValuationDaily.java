package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fact_index_valuation_daily", schema = "stellar_dm")
public class IndexValuationDaily {

    @EmbeddedId
    private IndexValuationDailyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("symbolSk")
    @JoinColumn(name = "symbol_sk", nullable = false)
    private DimSymbol symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dateSk")
    @JoinColumn(name = "date_sk", nullable = false)
    private DimDate date;

    @Column(name = "index_close")
    private BigDecimal indexClose;

    @Column(name = "pe")
    private BigDecimal pe;

    @Column(name = "pb")
    private BigDecimal pb;

    @Column(name = "ingestion_time", nullable = false)
    private LocalDateTime ingestionTime;
}
