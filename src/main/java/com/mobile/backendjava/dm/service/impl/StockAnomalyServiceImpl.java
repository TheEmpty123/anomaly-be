package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.anomaly.StockAnomalyDTO;
import com.mobile.backendjava.dm.entities.StockAnomaly;
import com.mobile.backendjava.dm.repository.StockAnomalyRepository;
import com.mobile.backendjava.dm.service.StockAnomalyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockAnomalyServiceImpl extends AService implements StockAnomalyService {

    private final StockAnomalyRepository stockAnomalyRepository;

    public StockAnomalyServiceImpl(StockAnomalyRepository stockAnomalyRepository) {
        this.stockAnomalyRepository = stockAnomalyRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<StockAnomalyDTO> getByPredictionDate(LocalDate predictionDate) {
        return runTask("getStockAnomalies",
                details(detail("predictionDate", predictionDate)),
                () -> {
                    LocalDate resolvedDate = predictionDate;
                    if (resolvedDate == null) {
                        resolvedDate = stockAnomalyRepository.findLatestPredictionDate();
                    }
                    if (resolvedDate == null) {
                        return List.of();
                    }
                    return stockAnomalyRepository.findByPredictionDateOrderBySymbolAscIdAsc(resolvedDate)
                            .stream()
                            .map(this::toDto)
                            .toList();
                });
    }

    private StockAnomalyDTO toDto(StockAnomaly anomaly) {
        return StockAnomalyDTO.builder()
                .id(anomaly.getId())
                .symbol(anomaly.getSymbol())
                .sectorGroup(anomaly.getSectorGroup())
                .groupName(anomaly.getGroupName())
                .predictionDate(anomaly.getPredictionDate())
                .score(anomaly.getScore())
                .p95(anomaly.getP95())
                .p98(anomaly.getP98())
                .scoreOverP95(anomaly.getScoreOverP95())
                .scoreOverP98(anomaly.getScoreOverP98())
                .baselineScore(anomaly.getBaselineScore())
                .baselineWindows(anomaly.getBaselineWindows())
                .scoreRatioVsBaseline(anomaly.getScoreRatioVsBaseline())
                .anomalyCode(anomaly.getAnomalyCode())
                .anomalyLevel(anomaly.getAnomalyLevel())
                .relativeLevel(anomaly.getRelativeLevel())
                .finalDecision(anomaly.getFinalDecision())
                .build();
    }
}
