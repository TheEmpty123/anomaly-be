package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.OhlcvDTO;
import com.mobile.backendjava.dm.entities.StockOhlcv;
import com.mobile.backendjava.dm.repository.StockOhlcvRepository;
import com.mobile.backendjava.dm.service.OhlcvService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class OhlcvServiceImpl extends AService implements OhlcvService {

    private static final int DEFAULT_SYMBOL_LIMIT = 500;
    private static final int MAX_SYMBOL_LIMIT = 5000;
    private static final int DEFAULT_MARKET_LIMIT = 199;
    private static final int MAX_MARKET_LIMIT = 1000;
    private static final String DEFAULT_TIMEFRAME = "1D";

    private final StockOhlcvRepository stockOhlcvRepository;

    public OhlcvServiceImpl(StockOhlcvRepository stockOhlcvRepository) {
        this.stockOhlcvRepository = stockOhlcvRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<OhlcvDTO> getBySymbol(String symbol, String timeframe, Integer fromDateSk,
                                      Integer toDateSk, Integer limit, String order) {
        return runTask("getOhlcvBySymbol", () -> {
            String normalizedOrder = normalizeOrder(order);
            Sort sort = Sort.by(direction(normalizedOrder), "id.dateSk", "id.timeSk");
            PageRequest pageRequest = PageRequest.of(0, capLimit(limit, DEFAULT_SYMBOL_LIMIT, MAX_SYMBOL_LIMIT), sort);
            return stockOhlcvRepository.findBySymbol(
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
    public List<OhlcvDTO> getByDate(Integer dateSk, String timeframe, Integer limit) {
        return runTask("getOhlcvByDate", () -> getMarketSnapshot(dateSk, timeframe, limit));
    }

    @Override
    public List<OhlcvDTO> getLatest(String timeframe, Integer limit) {
        return runTask("getLatestOhlcv", () -> {
            String tf = normalizeTimeframe(timeframe);
            Integer latestDateSk = stockOhlcvRepository.findMaxDateSkByTimeframe(tf);
            if (latestDateSk == null) {
                return List.of();
            }
            return getMarketSnapshot(latestDateSk, tf, limit);
        });
    }

    private List<OhlcvDTO> getMarketSnapshot(Integer dateSk, String timeframe, Integer limit) {
        Sort sort = Sort.by(Sort.Order.desc("marketCap").nullsLast());
        PageRequest pageRequest = PageRequest.of(0, capLimit(limit, DEFAULT_MARKET_LIMIT, MAX_MARKET_LIMIT), sort);
        return stockOhlcvRepository.findMarketSnapshot(dateSk, normalizeTimeframe(timeframe), pageRequest)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private String normalizeTimeframe(String timeframe) {
        if (timeframe == null || timeframe.isBlank()) {
            return DEFAULT_TIMEFRAME;
        }
        return timeframe.trim();
    }

    private String normalizeOrder(String order) {
        if (order == null || order.isBlank()) {
            return "asc";
        }
        String normalized = order.trim().toLowerCase();
        if (!normalized.equals("asc") && !normalized.equals("desc")) {
            throw new IllegalArgumentException("order must be asc or desc");
        }
        return normalized;
    }

    private Sort.Direction direction(String order) {
        return "desc".equals(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private int capLimit(Integer limit, int defaultLimit, int maxLimit) {
        if (limit == null) {
            return defaultLimit;
        }
        if (limit < 1) {
            return defaultLimit;
        }
        return Math.min(limit, maxLimit);
    }

    private OhlcvDTO toDto(StockOhlcv ohlcv) {
        return OhlcvDTO.builder()
                .symbol(ohlcv.getSymbol().getSymbol())
                .symbolSk(ohlcv.getId().getSymbolSk())
                .dateSk(ohlcv.getId().getDateSk())
                .fullDate(ohlcv.getDate().getFullDate())
                .timeframe(ohlcv.getId().getTimeframe())
                .timeSk(ohlcv.getId().getTimeSk())
                .open(ohlcv.getOpen())
                .high(ohlcv.getHigh())
                .low(ohlcv.getLow())
                .close(ohlcv.getClose())
                .volume(ohlcv.getVolume())
                .value(ohlcv.getValue())
                .marketCap(ohlcv.getMarketCap())
                .marketWeight(ohlcv.getMarketWeight())
                .build();
    }
}
