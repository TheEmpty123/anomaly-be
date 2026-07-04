package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.ForeignFlowChartDTO;
import com.mobile.backendjava.dm.dto.market.ForeignFlowHeatmapDTO;
import com.mobile.backendjava.dm.entities.ForeignFlowChartCache;
import com.mobile.backendjava.dm.entities.ForeignFlowHeatmapCache;
import com.mobile.backendjava.dm.repository.ForeignFlowChartCacheRepository;
import com.mobile.backendjava.dm.repository.ForeignFlowHeatmapCacheRepository;
import com.mobile.backendjava.dm.service.ForeignFlowService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ForeignFlowServiceImpl extends AService implements ForeignFlowService {

    private static final int DEFAULT_CHART_LIMIT = 500;
    private static final int DEFAULT_HEATMAP_LIMIT = 199;
    private static final int MAX_LIMIT = 1000;
    private static final Set<String> VALID_DIRECTIONS = Set.of("BUY", "SELL", "NEUTRAL");

    private final ForeignFlowChartCacheRepository chartRepository;
    private final ForeignFlowHeatmapCacheRepository heatmapRepository;

    public ForeignFlowServiceImpl(ForeignFlowChartCacheRepository chartRepository,
                                  ForeignFlowHeatmapCacheRepository heatmapRepository) {
        this.chartRepository = chartRepository;
        this.heatmapRepository = heatmapRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<ForeignFlowChartDTO> getChart(String entityType, String entityCode, String timeframe,
                                              Integer fromDateSk, Integer toDateSk, Integer limit) {
        return runTask("getForeignFlowChart",
                details(
                        detail("entityType", entityType),
                        detail("entityCode", entityCode),
                        detail("timeframe", timeframe),
                        detail("fromDateSk", fromDateSk),
                        detail("toDateSk", toDateSk),
                        detail("limit", capLimit(limit, DEFAULT_CHART_LIMIT))),
                () -> {
            String safeEntityType = requiredUpper(entityType, "entityType");
            String safeEntityCode = requiredUpper(entityCode, "entityCode");
            String safeTimeframe = requiredUpper(timeframe, "timeframe");
            PageRequest pageRequest = PageRequest.of(
                    0,
                    capLimit(limit, DEFAULT_CHART_LIMIT),
                    Sort.by(Sort.Direction.ASC, "id.dateSk"));

            return chartRepository.findChart(safeEntityType, safeEntityCode, safeTimeframe, fromDateSk, toDateSk, pageRequest)
                    .stream()
                    .map(this::toChartDto)
                    .toList();
        });
    }

    @Override
    public List<ForeignFlowHeatmapDTO> getHeatmap(Integer dateSk, String timeframe, Integer limit, String direction) {
        return runTask("getForeignFlowHeatmap",
                details(
                        detail("dateSk", dateSk),
                        detail("timeframe", timeframe),
                        detail("limit", capLimit(limit, DEFAULT_HEATMAP_LIMIT)),
                        detail("direction", direction)),
                () -> {
            if (dateSk == null) {
                throw new IllegalArgumentException("dateSk is required");
            }
            String safeTimeframe = requiredUpper(timeframe, "timeframe");
            String safeDirection = normalizeDirection(direction);
            PageRequest pageRequest = PageRequest.of(0, capLimit(limit, DEFAULT_HEATMAP_LIMIT), heatmapSort(safeDirection));

            List<ForeignFlowHeatmapCache> rows;
            if (safeDirection == null || safeDirection.equals("NEUTRAL")) {
                rows = heatmapRepository.findHeatmapOrderByAbsNetVal(dateSk, safeTimeframe, safeDirection, pageRequest);
            } else {
                rows = heatmapRepository.findHeatmap(dateSk, safeTimeframe, safeDirection, pageRequest);
            }
            return rows.stream().map(this::toHeatmapDto).toList();
        });
    }

    private String requiredUpper(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return null;
        }
        String normalized = direction.trim().toUpperCase(Locale.ROOT);
        if (!VALID_DIRECTIONS.contains(normalized)) {
            throw new IllegalArgumentException("direction must be BUY, SELL, or NEUTRAL");
        }
        return normalized;
    }

    private Sort heatmapSort(String direction) {
        if ("BUY".equals(direction)) {
            return Sort.by(Sort.Order.desc("cumulativeNetVal").nullsLast());
        }
        if ("SELL".equals(direction)) {
            return Sort.by(Sort.Order.asc("cumulativeNetVal").nullsLast());
        }
        return Sort.unsorted();
    }

    private int capLimit(Integer limit, int defaultLimit) {
        if (limit == null || limit < 1) {
            return defaultLimit;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private ForeignFlowChartDTO toChartDto(ForeignFlowChartCache entity) {
        return ForeignFlowChartDTO.builder()
                .entityType(entity.getId().getEntityType())
                .entityCode(entity.getId().getEntityCode())
                .dateSk(entity.getId().getDateSk())
                .timeframe(entity.getId().getTimeframe())
                .buyVal(entity.getBuyVal())
                .sellVal(entity.getSellVal())
                .netVal(entity.getNetVal())
                .cumulativeNetVal(entity.getCumulativeNetVal())
                .buyVol(entity.getBuyVol())
                .sellVol(entity.getSellVol())
                .netVol(entity.getNetVol())
                .cumulativeNetVol(entity.getCumulativeNetVol())
                .close(entity.getClose())
                .priceIndex100(entity.getPriceIndex100())
                .benchmarkCode(entity.getBenchmarkCode())
                .benchmarkClose(entity.getBenchmarkClose())
                .benchmarkIndex100(entity.getBenchmarkIndex100())
                .ingestionTime(entity.getIngestionTime())
                .build();
    }

    private ForeignFlowHeatmapDTO toHeatmapDto(ForeignFlowHeatmapCache entity) {
        return ForeignFlowHeatmapDTO.builder()
                .dateSk(entity.getId().getDateSk())
                .timeframe(entity.getId().getTimeframe())
                .symbol(entity.getId().getSymbol())
                .symbolSk(entity.getSymbolSk())
                .netVal(entity.getNetVal())
                .cumulativeNetVal(entity.getCumulativeNetVal())
                .netVol(entity.getNetVol())
                .cumulativeNetVol(entity.getCumulativeNetVol())
                .close(entity.getClose())
                .pctChange(entity.getPctChange())
                .volume(entity.getVolume())
                .value(entity.getValue())
                .marketCap(entity.getMarketCap())
                .marketWeight(entity.getMarketWeight())
                .rankNetBuy(entity.getRankNetBuy())
                .rankNetSell(entity.getRankNetSell())
                .intensity(entity.getIntensity())
                .direction(entity.getDirection())
                .ingestionTime(entity.getIngestionTime())
                .build();
    }
}
