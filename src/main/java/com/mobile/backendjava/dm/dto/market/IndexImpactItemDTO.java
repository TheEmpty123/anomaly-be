package com.mobile.backendjava.dm.dto.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexImpactItemDTO {

    private String symbol;
    private Double price;

    @JsonProperty("pct_change")
    private Double pctChange;

    private Double freefloat;

    @JsonProperty("capping_factor")
    private Double cappingFactor;

    @JsonProperty("adjusted_cap")
    private Double adjustedCap;

    private Double weight;

    @JsonProperty("impact_point")
    private Double impactPoint;

    @JsonProperty("last_updated")
    private String lastUpdated;
}
