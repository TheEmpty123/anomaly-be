package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.market.SymbolDTO;
import com.mobile.backendjava.dm.entities.DimSymbol;
import com.mobile.backendjava.dm.repository.DimSymbolRepository;
import com.mobile.backendjava.dm.service.SymbolService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SymbolServiceImpl extends AService implements SymbolService {

    private static final int DEFAULT_LIMIT = 500;
    private static final int MAX_LIMIT = 2000;

    private final DimSymbolRepository dimSymbolRepository;

    public SymbolServiceImpl(DimSymbolRepository dimSymbolRepository) {
        this.dimSymbolRepository = dimSymbolRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<SymbolDTO> getSymbols(Boolean activeOnly, Integer limit) {
        return runTask("getSymbols",
                details(
                        detail("activeOnly", activeOnly),
                        detail("limit", capLimit(limit))),
                () -> {
            boolean filterActive = activeOnly == null || activeOnly;
            PageRequest pageRequest = PageRequest.of(0, capLimit(limit), Sort.by(Sort.Direction.ASC, "symbol"));
            return dimSymbolRepository.findSymbols(filterActive, pageRequest)
                    .stream()
                    .map(this::toDto)
                    .toList();
        });
    }

    private int capLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private SymbolDTO toDto(DimSymbol symbol) {
        return SymbolDTO.builder()
                .symbolSk(symbol.getSymbolId())
                .symbol(symbol.getSymbol())
                .isActive(symbol.getIsActive())
                .sharesOutstanding(symbol.getSharesOutstanding())
                .freefloat(symbol.getFreefloat())
                .build();
    }
}
