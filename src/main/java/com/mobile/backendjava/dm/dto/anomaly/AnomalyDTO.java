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
public class AnomalyDTO {

    private String symbol;

    private String date;

    @JsonProperty("ai_analysis")
    private AIAnalysisDTO aiAnalysis;

    private String explanation;
}
