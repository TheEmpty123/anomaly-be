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
@Table(name = "foreign_flow_heatmap_cache", schema = "stellar_dm")
public class ForeignFlowHeatmapCache {

    @EmbeddedId
    private ForeignFlowHeatmapCacheId id;

    @Column(name = "symbol_sk")
    private Integer symbolSk;

    @Column(name = "net_val")
    private BigDecimal netVal;

    @Column(name = "cumulative_net_val")
    private BigDecimal cumulativeNetVal;

    @Column(name = "net_vol")
    private Long netVol;

    @Column(name = "cumulative_net_vol")
    private Long cumulativeNetVol;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "pct_change")
    private BigDecimal pctChange;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "market_cap")
    private BigDecimal marketCap;

    @Column(name = "market_weight")
    private BigDecimal marketWeight;

    @Column(name = "rank_net_buy")
    private Integer rankNetBuy;

    @Column(name = "rank_net_sell")
    private Integer rankNetSell;

    @Column(name = "intensity")
    private BigDecimal intensity;

    @Column(name = "direction")
    private String direction;

    @Column(name = "ingestion_time")
    private LocalDateTime ingestionTime;
}
