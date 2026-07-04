package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.OhlcvDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OhlcvService extends IInitializerData {
    List<OhlcvDTO> getBySymbol(String symbol, String timeframe, Integer fromDateSk,
                               Integer toDateSk, Integer limit, String order);

    List<OhlcvDTO> getByDate(Integer dateSk, String timeframe, Integer limit);

    List<OhlcvDTO> getLatest(String timeframe, Integer limit);
}
