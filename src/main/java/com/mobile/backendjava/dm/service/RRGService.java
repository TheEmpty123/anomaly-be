package com.mobile.backendjava.dm.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RRGService extends IInitializerData {
    List<Map<String, Object>> getSectorRRG(String regime, String benchmark, Integer dateSk);
}
