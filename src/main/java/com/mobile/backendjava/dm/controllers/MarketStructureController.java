//package com.mobile.backendjava.dm.controllers;
//
//import com.mobile.backendjava.dm.service.MarketStructureService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("${api.jpa.base-path}")
//public class MarketStructureController {
//
//    private final MarketStructureService marketStructureService;
//
//    public MarketStructureController(MarketStructureService marketStructureService) {
//        this.marketStructureService = marketStructureService;
//    }
//
//    @GetMapping("/market-structure")
//    public ResponseEntity<Map<String, Object>> latestMarketStructure(
//            @RequestParam String timeframe,
//            @RequestParam(required = false, defaultValue = "VNINDEX") String benchmark
//    ) {
//        if (timeframe == null || timeframe.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(marketStructureService.getLatest(timeframe.trim(), benchmark));
//    }
//}
