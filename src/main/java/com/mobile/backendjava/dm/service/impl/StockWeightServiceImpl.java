package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.StockWeightDTO;
import com.mobile.backendjava.dm.entities.StockOhlcv;
import com.mobile.backendjava.dm.repository.jpa.StockOhlcvRepository;
import com.mobile.backendjava.dm.service.StockWeightService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class StockWeightServiceImpl extends AService implements StockWeightService {

    private static final int DEFAULT_LIMIT = 1000;
    private static final int MAX_LIMIT = 5000;
    private static final String DEFAULT_TIMEFRAME = "1d";

    private final StockOhlcvRepository stockOhlcvRepository;

    public StockWeightServiceImpl(StockOhlcvRepository stockOhlcvRepository) {
        this.stockOhlcvRepository = stockOhlcvRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<StockWeightDTO> getWeights(String symbol, String sector, Integer dateSk, Integer fromDateSk,
                                           Integer toDateSk, String timeframe, Integer limit) {
        return runTask("getStockWeights",
                details(
                        detail("symbol", symbol),
                        detail("sector", sector),
                        detail("dateSk", dateSk),
                        detail("fromDateSk", fromDateSk),
                        detail("toDateSk", toDateSk),
                        detail("timeframe", normalizeTimeframe(timeframe)),
                        detail("limit", capLimit(limit))),
                () -> {
                    if (fromDateSk != null && toDateSk != null && fromDateSk > toDateSk) {
                        throw new IllegalArgumentException("fromDateSk must be less than or equal to toDateSk");
                    }

                    String tf = normalizeTimeframe(timeframe);
                    String normalizedSymbol = normalizeCode(symbol);
                    String normalizedSector = normalizeCode(sector);
                    boolean rangeQuery = dateSk == null && (fromDateSk != null || toDateSk != null);
                    Integer resolvedDateSk = rangeQuery ? null : dateSk;

                    if (!rangeQuery && resolvedDateSk == null) {
                        resolvedDateSk = stockOhlcvRepository.findMaxDateSkByTimeframe(tf);
                    }
                    if (!rangeQuery && resolvedDateSk == null) {
                        log.info("event=service.decision service=StockWeightServiceImpl action=getWeights outcome=empty reason=no-data timeframe={}", tf);
                        return List.of();
                    }

                    PageRequest pageRequest = PageRequest.of(0, capLimit(limit), rangeQuery ? rangeSort() : snapshotSort());
                    List<StockOhlcv> rows = stockOhlcvRepository.findWeights(
                            normalizedSymbol,
                            normalizedSector,
                            resolvedDateSk,
                            rangeQuery ? fromDateSk : null,
                            rangeQuery ? toDateSk : null,
                            tf,
                            pageRequest);

                    if (rows.isEmpty()) {
                        log.info("event=service.decision service=StockWeightServiceImpl action=getWeights outcome=empty reason=no-matching-rows timeframe={} symbol={} sector={} dateSk={} fromDateSk={} toDateSk={}",
                                tf, normalizedSymbol, normalizedSector, resolvedDateSk, fromDateSk, toDateSk);
                        return List.of();
                    }

                    return rows.stream()
                            .map(this::toDto)
                            .toList();
                });
    }

    private String normalizeTimeframe(String timeframe) {
        if (timeframe == null || timeframe.isBlank()) {
            return DEFAULT_TIMEFRAME;
        }
        return timeframe.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private int capLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private Sort snapshotSort() {
        return Sort.by(
                Sort.Order.desc("marketWeight").nullsLast(),
                Sort.Order.asc("symbol.symbol"));
    }

    private Sort rangeSort() {
        return Sort.by(
                Sort.Order.desc("id.dateSk"),
                Sort.Order.desc("marketWeight").nullsLast(),
                Sort.Order.asc("symbol.symbol"));
    }

    private StockWeightDTO toDto(StockOhlcv ohlcv) {
        return StockWeightDTO.builder()
                .symbol(ohlcv.getSymbol() == null ? null : ohlcv.getSymbol().getSymbol())
                .symbolSk(ohlcv.getId() == null ? null : ohlcv.getId().getSymbolSk())
                .companyName(ohlcv.getSymbol() == null ? null : ohlcv.getSymbol().getCompanyName())
                .sector(ohlcv.getSymbol() == null ? null : ohlcv.getSymbol().getSector())
                .dateSk(ohlcv.getId() == null ? null : ohlcv.getId().getDateSk())
                .fullDate(ohlcv.getDate() == null ? null : ohlcv.getDate().getFullDate())
                .timeframe(ohlcv.getId() == null ? null : ohlcv.getId().getTimeframe())
                .timeSk(ohlcv.getId() == null ? null : ohlcv.getId().getTimeSk())
                .close(ohlcv.getClose())
                .volume(ohlcv.getVolume())
                .value(ohlcv.getValue())
                .marketCap(ohlcv.getMarketCap())
                .marketWeight(ohlcv.getMarketWeight())
                .build();
    }
}
