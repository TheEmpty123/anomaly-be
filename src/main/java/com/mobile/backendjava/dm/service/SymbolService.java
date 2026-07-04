package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.SymbolDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SymbolService extends IInitializerData {
    List<SymbolDTO> getSymbols(Boolean activeOnly, Integer limit);
}
