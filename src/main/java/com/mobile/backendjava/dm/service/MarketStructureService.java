package com.mobile.backendjava.dm.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MarketStructureService extends IInitializerData{
    /**
     * Get the latest market structure row for the given timeframe and benchmark.
     * timeframe accepts 1M, 3M, 6M, 1Y (case-insensitive)
     * benchmark defaults to VNINDEX if null/blank.
     */
    Map<String, Object> getLatest(String timeframe, String benchmark);
}
