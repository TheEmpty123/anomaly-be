package com.mobile.backendjava.dm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "dim_date", schema = "stellar_dm")
public class DimDate {

    @Id
    @Column(name = "date_id", nullable = false)
    private Integer dateId;

    @Column(name = "full_date", nullable = false)
    private LocalDate fullDate;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "day")
    private Integer day;

    @Column(name = "quarter")
    private Integer quarter;

    @Column(name = "week")
    private Integer week;

    @Column(name = "is_trading_day")
    private Boolean isTradingDay;
}
