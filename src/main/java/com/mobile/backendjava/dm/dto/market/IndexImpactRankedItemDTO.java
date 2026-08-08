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
public class IndexImpactRankedItemDTO {

    private String symbol;

    @JsonProperty("impact_point")
    private Double impactPoint;

    private Double weight;

    @JsonProperty("pct_change")
    private Double pctChange;
}
