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
@Table(name = "sector_performance_chart_cache", schema = "stellar_dm")
public class SectorPerformance {

    @EmbeddedId
    private SectorPerformanceId id;

    @Column(name = "block_type")
    private String blockType;

    @Column(name = "chart_data", columnDefinition = "jsonb")
    private String chartData;

    @Column(name = "ingestion_time")
    private LocalDateTime ingestionTime;
}
