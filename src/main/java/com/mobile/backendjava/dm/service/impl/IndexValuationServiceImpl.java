package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.IndexValuationDTO;
import com.mobile.backendjava.dm.entities.IndexValuationDaily;
import com.mobile.backendjava.dm.repository.IndexValuationDailyRepository;
import com.mobile.backendjava.dm.service.IndexValuationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class IndexValuationServiceImpl extends AService implements IndexValuationService {

    private final IndexValuationDailyRepository indexValuationDailyRepository;

    public IndexValuationServiceImpl(IndexValuationDailyRepository indexValuationDailyRepository) {
        this.indexValuationDailyRepository = indexValuationDailyRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<IndexValuationDTO> getHistorical(String symbol, LocalDate startDate, LocalDate endDate) {
        return runTask("getIndexValuationHistorical",
                details(
                        detail("symbol", symbol),
                        detail("startDate", startDate),
                        detail("endDate", endDate)),
                () -> {
                    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                        throw new IllegalArgumentException("start_date must be less than or equal to end_date");
                    }

                    String normalizedSymbol = normalizeSymbol(symbol);
                    List<IndexValuationDTO> result = indexValuationDailyRepository
                            .findHistoricalBySymbol(normalizedSymbol, startDate, endDate)
                            .stream()
                            .map(this::toDto)
                            .toList();
                    if (result.isEmpty()) {
                        log.info("event=service.decision service=IndexValuationServiceImpl action=getHistorical outcome=empty reason=no-matching-rows symbol={} startDate={} endDate={}",
                                normalizedSymbol, startDate, endDate);
                    }
                    return result;
                });
    }

    @Override
    public Optional<IndexValuationDTO> getLatest(String symbol) {
        return runTask("getLatestIndexValuation",
                details(detail("symbol", symbol)),
                () -> {
                    String normalizedSymbol = normalizeSymbol(symbol);
                    Optional<IndexValuationDTO> result = indexValuationDailyRepository.findLatestBySymbol(normalizedSymbol, PageRequest.of(0, 1))
                            .stream()
                            .findFirst()
                            .map(this::toDto);
                    if (result.isEmpty()) {
                        log.info("event=service.decision service=IndexValuationServiceImpl action=getLatest outcome=empty reason=no-matching-rows symbol={}",
                                normalizedSymbol);
                    }
                    return result;
                });
    }

    private String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be blank");
        }
        return symbol.trim().toUpperCase(Locale.ROOT);
    }

    private IndexValuationDTO toDto(IndexValuationDaily valuation) {
        return IndexValuationDTO.builder()
                .symbol(valuation.getSymbol().getSymbol())
                .date(valuation.getDate().getFullDate())
                .indexClose(valuation.getIndexClose())
                .pe(valuation.getPe())
                .pb(valuation.getPb())
                .build();
    }
}
