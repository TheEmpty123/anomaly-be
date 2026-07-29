package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_anomalies", schema = "stellar_dm")
public class StockAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "sector_group")
    private String sectorGroup;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "prediction_date")
    private LocalDate predictionDate;

    @Column(name = "score")
    private Double score;

    @Column(name = "p95")
    private Double p95;

    @Column(name = "p98")
    private Double p98;

    @Column(name = "score_over_p95")
    private Double scoreOverP95;

    @Column(name = "score_over_p98")
    private Double scoreOverP98;

    @Column(name = "baseline_score")
    private Double baselineScore;

    @Column(name = "baseline_windows")
    private Integer baselineWindows;

    @Column(name = "score_ratio_vs_baseline")
    private Double scoreRatioVsBaseline;

    @Column(name = "anomaly_code")
    private Integer anomalyCode;

    @Column(name = "anomaly_level")
    private String anomalyLevel;

    @Column(name = "relative_level")
    private String relativeLevel;

    @Column(name = "final_decision")
    private String finalDecision;
}
