package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.StockWeightDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StockWeightService extends IInitializerData {

    List<StockWeightDTO> getWeights(
            String symbol,
            String sector,
            Integer dateSk,
            Integer fromDateSk,
            Integer toDateSk,
            String timeframe,
            Integer limit);
}
