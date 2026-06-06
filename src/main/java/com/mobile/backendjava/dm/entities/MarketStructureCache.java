package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "market_structure_cache", schema = "stellar_dm")
public class MarketStructureCache {

    @EmbeddedId
    private MarketStructureCacheId id;

    @Column(name = "market_structure_code", length = 30, nullable = false)
    private String marketStructureCode;

    @Column(name = "market_structure_label", length = 100, nullable = false)
    private String marketStructureLabel;

    // Store jsonb as String; you can switch to a JSON type handler later if needed
    @Column(name = "core_sectors", columnDefinition = "jsonb", nullable = false)
    private String coreSectors;

    @Column(name = "core_blocks", columnDefinition = "jsonb", nullable = false)
    private String coreBlocks;

    @Column(name = "top_ecosystem_code", length = 50)
    private String topEcosystemCode;

    @Column(name = "top_ecosystem_name", length = 100)
    private String topEcosystemName;

    @Column(name = "sector_rankings", columnDefinition = "jsonb", nullable = false)
    private String sectorRankings;

    @Column(name = "ecosystem_rankings", columnDefinition = "jsonb", nullable = false)
    private String ecosystemRankings;

    @Column(name = "ingestion_time")
    private LocalDateTime ingestionTime;
}
