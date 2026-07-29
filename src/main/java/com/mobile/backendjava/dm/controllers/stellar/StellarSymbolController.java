package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.SymbolDTO;
import com.mobile.backendjava.dm.service.SymbolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/symbols")
@Slf4j
public class StellarSymbolController {

    private final SymbolService symbolService;

    public StellarSymbolController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @GetMapping
    public ResponseEntity<List<SymbolDTO>> getSymbols(
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly,
            @RequestParam(required = false) Integer limit) {
        log.info("event=controller.request action=symbols.list activeOnly={} limit={}", activeOnly, limit);
        List<SymbolDTO> result = symbolService.getSymbols(activeOnly, limit);
        log.info("event=controller.response action=symbols.list status=200 resultCount={}", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableSymbols() {
        log.info("event=controller.request action=symbols.available");
        List<String> result = symbolService.getAvailableSymbols();
        log.info("event=controller.response action=symbols.available status=200 resultCount={}", result.size());
        return ResponseEntity.ok(result);
    }
}
