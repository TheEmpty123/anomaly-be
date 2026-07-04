package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.entities.MarketStructureCache;
import com.mobile.backendjava.dm.model.Timeframe;
import com.mobile.backendjava.dm.repository.MarketStructureRepository;
import com.mobile.backendjava.dm.service.MarketStructureService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MarketStructureServiceImpl extends AService implements MarketStructureService {

    private final MarketStructureRepository marketStructureRepository;

    public MarketStructureServiceImpl(MarketStructureRepository marketStructureRepository) {
        this.marketStructureRepository = marketStructureRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public Map<String, Object> getLatest(String timeframe, String benchmark) {
        return runTask("getLatestMarketStructure",
                details(detail("timeframe", timeframe), detail("benchmark", benchmark)),
                () -> {
            Timeframe tf = Timeframe.fromString(timeframe);
            if (tf == null) {
                return Map.of();
            }
            String tfCode = tf.getCode();
            String safeBenchmark = (benchmark == null || benchmark.isBlank()) ? "VNINDEX" : benchmark.trim();

            Integer maxDateSk = marketStructureRepository.findMaxDateSkByTimeframeAndBenchmark(tfCode, safeBenchmark);
            if (maxDateSk == null) {
                return Map.of();
            }
            Optional<MarketStructureCache> opt = marketStructureRepository
                    .findByIdDateSkAndIdTimeframeAndIdBenchmark(maxDateSk, tfCode, safeBenchmark);
            if (opt.isEmpty()) {
                return Map.of();
            }
            MarketStructureCache e = opt.get();
            Map<String, Object> m = new HashMap<>();
            m.put("date_sk", e.getId().getDateSk());
            m.put("timeframe", e.getId().getTimeframe());
            m.put("benchmark", e.getId().getBenchmark());
            m.put("market_structure_code", e.getMarketStructureCode());
            m.put("market_structure_label", e.getMarketStructureLabel());
            m.put("core_sectors", e.getCoreSectors());
            m.put("core_blocks", e.getCoreBlocks());
            m.put("top_ecosystem_code", e.getTopEcosystemCode());
            m.put("top_ecosystem_name", e.getTopEcosystemName());
            m.put("sector_rankings", e.getSectorRankings());
            m.put("ecosystem_rankings", e.getEcosystemRankings());
            m.put("ingestion_time", e.getIngestionTime());
            return m;
        });
    }
}
