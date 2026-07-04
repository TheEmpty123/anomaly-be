package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.repository.DataMartRepository;
import com.mobile.backendjava.dm.service.DataMartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * This file is a template, DO NOT MODIFY OR USE IT
 */
@Service
public class DataMartServiceImpl extends AService implements DataMartService {

    private final DataMartRepository repository;

    public DataMartServiceImpl(DataMartRepository repository) {
        this.repository = repository;
        initLogger();
    }

    @Override
    public int ping() {
        return runTask("pingDataMart", "repository=DataMartRepository action=ping", repository::ping);
    }

    @Override
    public Map<String, Object> info() {
        return runTask("getDataMartInfo", "repository=DataMartRepository action=info", repository::info);
    }

    @Override
    public List<Map<String, Object>> getSectorRRG(String regime, String benchmark, Integer dateSk) {
        return runTask("getDataMartSectorRRG",
                details(detail("regime", regime), detail("benchmark", benchmark), detail("dateSk", dateSk)),
                () -> {
            // Validate and normalize regime using enum; return empty if invalid
            com.mobile.backendjava.dm.model.Regime r = com.mobile.backendjava.dm.model.Regime.fromString(regime);
            if (r == null) {
                return List.of();
            }
            // Delegate to repository; when dateSk is null, repository returns the latest available date_sk
            return repository.getSectorRRG(r.name(), benchmark, dateSk);
        });
    }

    @Override
    public Map<String, Object> getMarketStructure(String timeframe, String benchmark, Integer dateSk, String regime) {
        return runTask("getDataMartMarketStructure",
                details(
                        detail("timeframe", timeframe),
                        detail("benchmark", benchmark),
                        detail("dateSk", dateSk),
                        detail("regime", regime)),
                () -> repository.getMarketStructure(timeframe, benchmark, dateSk, regime));
    }

    @Override
    public List<Map<String, Object>> getSectorPerformance(String timeframe, String sectorCode) {
        return runTask("getDataMartSectorPerformance",
                details(detail("timeframe", timeframe), detail("sectorCode", sectorCode)),
                () -> repository.getSectorPerformance(timeframe, sectorCode));
    }
}
