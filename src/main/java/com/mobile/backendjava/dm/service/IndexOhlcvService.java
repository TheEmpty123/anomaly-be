package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.IndexOhlcvDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IndexOhlcvService extends IInitializerData {
    List<IndexOhlcvDTO> getBySymbol(String symbol, String timeframe, Integer fromDateSk,
                                    Integer toDateSk, Integer limit, String order);

    List<IndexOhlcvDTO> getByDate(Integer dateSk, String timeframe, Integer limit);

    List<IndexOhlcvDTO> getLatest(String timeframe, Integer limit);
}
