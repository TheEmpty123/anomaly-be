package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.entities.SectorRrgCache;
import com.mobile.backendjava.dm.model.Regime;
import com.mobile.backendjava.dm.repository.RRGRepository;
import com.mobile.backendjava.dm.service.RRGService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RRGServiceImpl extends AService implements RRGService {

    private final RRGRepository rrgRepository;

    public RRGServiceImpl(RRGRepository rrgRepository) {
        this.rrgRepository = rrgRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<Map<String, Object>> getSectorRRG(String regime, String benchmark, Integer dateSk) {
        return runTask("getSectorRRG",
                details(detail("regime", regime), detail("benchmark", benchmark), detail("dateSk", dateSk)),
                () -> {
            Regime r = Regime.fromString(regime);
            if (r == null) {
                return List.of();
            }
            String safeBenchmark = (benchmark == null || benchmark.isBlank()) ? "VNINDEX" : benchmark.trim();
            Integer effectiveDate = dateSk;
            if (effectiveDate == null) {
                effectiveDate = rrgRepository.findMaxDateSkByRegimeAndBenchmark(r.name(), safeBenchmark);
                if (effectiveDate == null) {
                    return List.of();
                }
            }
            List<SectorRrgCache> rows = rrgRepository
                    .findAllByIdRegimeAndIdBenchmarkAndIdDateSkOrderByIdSectorCode(r.name(), safeBenchmark, effectiveDate);
            return rows.stream().map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("sector_code", e.getId().getSectorCode());
                m.put("date_sk", e.getId().getDateSk());
                m.put("rs", e.getRs());
                m.put("rm", e.getRm());
                m.put("phase", e.getPhase());
                m.put("stock_count", e.getStockCount());
                m.put("total_stocks", e.getTotalStocks());
                m.put("sector_name", e.getSectorName());
                m.put("sector_name_en", e.getSectorNameEn());
                m.put("block_type", e.getBlockType());
                m.put("top_stocks_by_cap", e.getTopStocksByCap()); // already stored as JSON string
                m.put("benchmark", e.getId().getBenchmark());
                LocalDateTime ingestionTime = e.getIngestionTime();
                m.put("ingestion_time", ingestionTime);
                m.put("regime", e.getId().getRegime());
                m.put("total_volume", e.getTotalVolume());
                m.put("total_value", e.getTotalValue());
                m.put("total_market_cap", e.getTotalMarketCap());
                m.put("avg_market_cap", e.getAvgMarketCap());
                m.put("liquidity_score", e.getLiquidityScore());
                m.put("total_freefloat_market_cap", e.getTotalFreefloatMarketCap());
                m.put("avg_market_weight", e.getAvgMarketWeight());
                return m;
            }).collect(Collectors.toList());
        });
    }
}
