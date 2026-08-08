package com.mobile.backendjava.dm.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexImpactHistoryPointDTO {

    private String timestamp;

    @JsonProperty("index_code")
    private String indexCode;

    @JsonProperty("index_value")
    private Double indexValue;

    @JsonProperty("valid_count")
    private Integer validCount;

    @JsonProperty("missing_count")
    private Integer missingCount;

    @JsonProperty("total_adjusted_cap")
    private Double totalAdjustedCap;

    @JsonProperty("total_impact_point")
    private Double totalImpactPoint;

    @JsonProperty("top_positive")
    private List<IndexImpactRankedItemDTO> topPositive;

    @JsonProperty("top_negative")
    private List<IndexImpactRankedItemDTO> topNegative;
}
