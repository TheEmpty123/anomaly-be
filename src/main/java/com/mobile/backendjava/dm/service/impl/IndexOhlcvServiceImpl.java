package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.IndexOhlcvDTO;
import com.mobile.backendjava.dm.entities.IndexOhlcv;
import com.mobile.backendjava.dm.repository.IndexOhlcvRepository;
import com.mobile.backendjava.dm.service.IndexOhlcvService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class IndexOhlcvServiceImpl extends AService implements IndexOhlcvService {

    private static final int DEFAULT_SYMBOL_LIMIT = 500;
    private static final int MAX_SYMBOL_LIMIT = 5000;
    private static final int DEFAULT_MARKET_LIMIT = 100;
    private static final int MAX_MARKET_LIMIT = 1000;
    private static final String DEFAULT_TIMEFRAME = "1d";

    private final IndexOhlcvRepository indexOhlcvRepository;

    public IndexOhlcvServiceImpl(IndexOhlcvRepository indexOhlcvRepository) {
        this.indexOhlcvRepository = indexOhlcvRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<IndexOhlcvDTO> getBySymbol(String symbol, String timeframe, Integer fromDateSk,
                                           Integer toDateSk, Integer limit, String order) {
        return runTask("getIndexOhlcvBySymbol",
                details(
                        detail("symbol", symbol),
                        detail("timeframe", normalizeTimeframe(timeframe)),
                        detail("fromDateSk", fromDateSk),
                        detail("toDateSk", toDateSk),
                        detail("limit", capLimit(limit, DEFAULT_SYMBOL_LIMIT, MAX_SYMBOL_LIMIT)),
                        detail("order", order)),
                () -> {
                    String normalizedOrder = normalizeOrder(order);
                    Sort sort = Sort.by(direction(normalizedOrder), "id.dateSk", "id.timeSk");
                    PageRequest pageRequest = PageRequest.of(0, capLimit(limit, DEFAULT_SYMBOL_LIMIT, MAX_SYMBOL_LIMIT), sort);
                    return indexOhlcvRepository.findBySymbol(
                                    symbol == null ? null : symbol.trim().toUpperCase(Locale.ROOT),
                                    normalizeTimeframe(timeframe),
                                    fromDateSk,
                                    toDateSk,
                                    pageRequest)
                            .stream()
                            .map(this::toDto)
                            .toList();
                });
    }

    @Override
    public List<IndexOhlcvDTO> getByDate(Integer dateSk, String timeframe, Integer limit) {
        return runTask("getIndexOhlcvByDate",
                details(
                        detail("dateSk", dateSk),
                        detail("timeframe", normalizeTimeframe(timeframe)),
                        detail("limit", capLimit(limit, DEFAULT_MARKET_LIMIT, MAX_MARKET_LIMIT))),
                () -> getMarketSnapshot(dateSk, timeframe, limit));
    }

    @Override
    public List<IndexOhlcvDTO> getLatest(String timeframe, Integer limit) {
        return runTask("getLatestIndexOhlcv",
                details(
                        detail("timeframe", normalizeTimeframe(timeframe)),
                        detail("limit", capLimit(limit, DEFAULT_MARKET_LIMIT, MAX_MARKET_LIMIT))),
                () -> {
                    String tf = normalizeTimeframe(timeframe);
                    Integer latestDateSk = indexOhlcvRepository.findMaxDateSkByTimeframe(tf);
                    if (latestDateSk == null) {
                        return List.of();
                    }
                    return getMarketSnapshot(latestDateSk, tf, limit);
                });
    }

    private List<IndexOhlcvDTO> getMarketSnapshot(Integer dateSk, String timeframe, Integer limit) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id.symbolSk");
        PageRequest pageRequest = PageRequest.of(0, capLimit(limit, DEFAULT_MARKET_LIMIT, MAX_MARKET_LIMIT), sort);
        return indexOhlcvRepository.findMarketSnapshot(dateSk, normalizeTimeframe(timeframe), pageRequest)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private String normalizeTimeframe(String timeframe) {
        if (timeframe == null || timeframe.isBlank()) {
            return DEFAULT_TIMEFRAME;
        }
        return timeframe.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOrder(String order) {
        if (order == null || order.isBlank()) {
            return "asc";
        }
        String normalized = order.trim().toLowerCase(Locale.ROOT);
        if (!normalized.equals("asc") && !normalized.equals("desc")) {
            throw new IllegalArgumentException("order must be asc or desc");
        }
        return normalized;
    }

    private Sort.Direction direction(String order) {
        return "desc".equals(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private int capLimit(Integer limit, int defaultLimit, int maxLimit) {
        if (limit == null || limit < 1) {
            return defaultLimit;
        }
        return Math.min(limit, maxLimit);
    }

    private IndexOhlcvDTO toDto(IndexOhlcv indexOhlcv) {
        return IndexOhlcvDTO.builder()
                .symbol(indexOhlcv.getSymbol().getSymbol())
                .symbolSk(indexOhlcv.getId().getSymbolSk())
                .dateSk(indexOhlcv.getId().getDateSk())
                .fullDate(indexOhlcv.getDate().getFullDate())
                .timeframe(indexOhlcv.getId().getTimeframe())
                .timeSk(indexOhlcv.getId().getTimeSk())
                .open(indexOhlcv.getOpen())
                .high(indexOhlcv.getHigh())
                .low(indexOhlcv.getLow())
                .close(indexOhlcv.getClose())
                .volume(indexOhlcv.getVolume())
                .value(indexOhlcv.getValue())
                .build();
    }
}
