package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.ForeignFlowChartDTO;
import com.mobile.backendjava.dm.dto.market.ForeignFlowHeatmapDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForeignFlowService extends IInitializerData {
    List<ForeignFlowChartDTO> getChart(String entityType, String entityCode, String timeframe,
                                       Integer fromDateSk, Integer toDateSk, Integer limit);

    List<ForeignFlowHeatmapDTO> getHeatmap(Integer dateSk, String timeframe, Integer limit, String direction);
}
