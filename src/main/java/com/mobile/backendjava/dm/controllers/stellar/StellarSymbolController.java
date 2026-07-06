package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.SymbolDTO;
import com.mobile.backendjava.dm.service.SymbolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.stellar.base-path}/symbols")
public class StellarSymbolController {

    private final SymbolService symbolService;

    public StellarSymbolController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @GetMapping
    public ResponseEntity<List<SymbolDTO>> getSymbols(
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(symbolService.getSymbols(activeOnly, limit));
    }

    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableSymbols() {
        return ResponseEntity.ok(symbolService.getAvailableSymbols());
    }
}
