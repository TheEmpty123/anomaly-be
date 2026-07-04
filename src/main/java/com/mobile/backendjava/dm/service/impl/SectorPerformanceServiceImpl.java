package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.entities.SectorPerformance;
import com.mobile.backendjava.dm.model.Timeframe;
import com.mobile.backendjava.dm.repository.SectorPerformanceRepository;
import com.mobile.backendjava.dm.service.SectorPerformanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SectorPerformanceServiceImpl extends AService implements SectorPerformanceService {

    private final SectorPerformanceRepository sectorPerformanceRepository;

    public SectorPerformanceServiceImpl(SectorPerformanceRepository sectorPerformanceRepository) {
        this.sectorPerformanceRepository = sectorPerformanceRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<Map<String, Object>> getLatest(String timeframe, String sectorCode) {
        return runTask("getLatestSectorPerformance", () -> {
            Timeframe tf = Timeframe.fromString(timeframe);
            if (tf == null) {
                return List.of();
            }
            String tfCode = tf.getCode();

            LocalDateTime latestIngestion = sectorPerformanceRepository.findMaxIngestionTimeByTimeframe(tfCode);
            if (latestIngestion == null) {
                return List.of();
            }

            List<SectorPerformance> rows;
            if (sectorCode == null || sectorCode.isBlank()) {
                rows = sectorPerformanceRepository
                        .findAllByIdTimeframeAndIngestionTimeOrderByIdSectorCode(tfCode, latestIngestion);
            } else {
                rows = sectorPerformanceRepository
                        .findAllByIdTimeframeAndIdSectorCodeAndIngestionTime(tfCode, sectorCode.trim(), latestIngestion);
            }

            return rows.stream().map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("sector_code", e.getId().getSectorCode());
                m.put("block_type", e.getBlockType());
                m.put("timeframe", e.getId().getTimeframe());
                m.put("chart_data", e.getChartData());
                m.put("ingestion_time", e.getIngestionTime());
                return m;
            }).collect(Collectors.toList());
        });
    }
}
