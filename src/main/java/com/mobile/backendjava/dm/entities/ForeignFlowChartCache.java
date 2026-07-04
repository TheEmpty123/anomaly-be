package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "foreign_flow_chart_cache", schema = "stellar_dm")
public class ForeignFlowChartCache {

    @EmbeddedId
    private ForeignFlowChartCacheId id;

    @Column(name = "buy_val")
    private BigDecimal buyVal;

    @Column(name = "sell_val")
    private BigDecimal sellVal;

    @Column(name = "net_val")
    private BigDecimal netVal;

    @Column(name = "cumulative_net_val")
    private BigDecimal cumulativeNetVal;

    @Column(name = "buy_vol")
    private Long buyVol;

    @Column(name = "sell_vol")
    private Long sellVol;

    @Column(name = "net_vol")
    private Long netVol;

    @Column(name = "cumulative_net_vol")
    private Long cumulativeNetVol;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "price_index_100")
    private BigDecimal priceIndex100;

    @Column(name = "benchmark_code")
    private String benchmarkCode;

    @Column(name = "benchmark_close")
    private BigDecimal benchmarkClose;

    @Column(name = "benchmark_index_100")
    private BigDecimal benchmarkIndex100;

    @Column(name = "ingestion_time")
    private LocalDateTime ingestionTime;
}
