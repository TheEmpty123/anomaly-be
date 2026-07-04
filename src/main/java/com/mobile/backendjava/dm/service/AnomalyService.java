package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.anomaly.AnomalyDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AnomalyService extends IInitializerData {

    List<AnomalyDTO> getAnomalies();
}
