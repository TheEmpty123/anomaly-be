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
@Table(name = "sector_rrg_cache", schema = "stellar_dm")
public class SectorRrgCache {

    @EmbeddedId
    private SectorRrgCacheId id;

    @Column(name = "rs", precision = 10, scale = 2)
    private BigDecimal rs;

    @Column(name = "rm", precision = 10, scale = 2)
    private BigDecimal rm;

    @Column(name = "phase", length = 20)
    private String phase;

    @Column(name = "stock_count")
    private Integer stockCount;

    @Column(name = "total_stocks")
    private Integer totalStocks;

    @Column(name = "sector_name")
    private String sectorName;

    @Column(name = "sector_name_en", length = 100)
    private String sectorNameEn;

    @Column(name = "block_type")
    private String blockType;

    @Column(name = "top_stocks_by_cap", columnDefinition = "jsonb")
    private String topStocksByCap;

    @Column(name = "ingestion_time")
    private LocalDateTime ingestionTime;
}
