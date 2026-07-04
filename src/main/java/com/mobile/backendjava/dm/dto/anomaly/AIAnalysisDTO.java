package com.mobile.backendjava.dm.dto.anomaly;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisDTO {

    @JsonProperty("anomaly_score")
    private Double anomalyScore;

    @JsonProperty("is_anomaly")
    private Boolean isAnomaly;

    @JsonProperty("status_label")
    private String statusLabel;
}
