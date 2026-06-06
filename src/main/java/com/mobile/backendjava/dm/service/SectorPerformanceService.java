package com.mobile.backendjava.dm.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SectorPerformanceService extends IInitializerData{
    /**
     * Get the latest sector performance chart rows for the given timeframe.
     * If sectorCode is provided, returns just that sector; otherwise returns all sectors ordered by code.
     */
    List<Map<String, Object>> getLatest(String timeframe, String sectorCode);
}
