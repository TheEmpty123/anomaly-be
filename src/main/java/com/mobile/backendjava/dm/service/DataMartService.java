package com.mobile.backendjava.dm.service;

import java.util.List;
import java.util.Map;

public interface DataMartService {
    int ping();
    Map<String, Object> info();
    List<Map<String, Object>> getSectorRRG(String regime, String benchmark, Integer dateSk);
    Map<String, Object> getMarketStructure(String timeframe, String benchmark, Integer dateSk, String regime);
    List<Map<String, Object>> getSectorPerformance(String timeframe, String sectorCode);
}
