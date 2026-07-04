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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_ohlcv", schema = "stellar_dm")
public class StockOhlcv {

    @EmbeddedId
    private StockOhlcvId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("symbolSk")
    @JoinColumn(name = "symbol_sk", nullable = false)
    private DimSymbol symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dateSk")
    @JoinColumn(name = "date_sk", nullable = false)
    private DimDate date;

    @Column(name = "open")
    private BigDecimal open;

    @Column(name = "high")
    private BigDecimal high;

    @Column(name = "low")
    private BigDecimal low;

    @Column(name = "close")
    private BigDecimal close;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "market_cap")
    private BigDecimal marketCap;

    @Column(name = "market_weight")
    private BigDecimal marketWeight;
}
