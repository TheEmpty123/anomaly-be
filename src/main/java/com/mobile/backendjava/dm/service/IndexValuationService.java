package com.mobile.backendjava.dm.service;

import com.mobile.backendjava.dm.dto.market.IndexValuationDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface IndexValuationService extends IInitializerData {
    List<IndexValuationDTO> getHistorical(String symbol, LocalDate startDate, LocalDate endDate);

    Optional<IndexValuationDTO> getLatest(String symbol);
}
