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
@Table(name = "dim_symbol", schema = "stellar_dm")
public class DimSymbol {

    @Id
    @Column(name = "symbol_id", nullable = false)
    private Integer symbolId;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "sector", length = 100)
    private String sector;

    @Column(name = "exchange_sk")
    private Integer exchangeSk;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "listing_date")
    private LocalDate listingDate;

    @Column(name = "is_current")
    private Integer isCurrent;
}
