package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.anomaly.StockAnomalyDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface StockAnomalyService extends IInitializerData {

    List<StockAnomalyDTO> getByPredictionDate(LocalDate predictionDate);
}
