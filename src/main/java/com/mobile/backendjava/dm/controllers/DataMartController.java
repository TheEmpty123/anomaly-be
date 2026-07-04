// package com.mobile.backendjava.dm.controllers;

// import com.mobile.backendjava.dm.service.DataMartService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.List;
// import java.util.Map;
/**
 * This file is a template, DO NOT MODIFY OR USE IT
 */
//@RestController
//@RequestMapping("/api")
//public class DataMartController {
//
//    private final DataMartService service;
//
//    public DataMartController(DataMartService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/ping")
//    public ResponseEntity<Map<String, Object>> ping() {
//        int one = service.ping();
//        Map<String, Object> body = Map.of(
//                "ok", one == 1,
//                "result", one
//        );
//        return ResponseEntity.ok(body);
//    }
//
//    @GetMapping("/info")
//    public ResponseEntity<Map<String, Object>> info() {
//        return ResponseEntity.ok(service.info());
//    }
//
//    // RRG endpoint
//    @GetMapping("/rrg")
//    public ResponseEntity<List<Map<String, Object>>> rrg(
//            @RequestParam String regime,
//            @RequestParam(required = false, defaultValue = "VNINDEX") String benchmark,
//            @RequestParam(required = false) Integer dateSk
//    ) {
//        if (regime == null || regime.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(service.getSectorRRG(regime.trim(), benchmark, dateSk));
//    }
//
//    // Market Structure endpoint
//    @GetMapping("/market-structure")
//    public ResponseEntity<Map<String, Object>> marketStructure(
//            @RequestParam String timeframe,
//            @RequestParam(required = false, defaultValue = "VNINDEX") String benchmark,
//            @RequestParam(required = false) Integer dateSk,
//            @RequestParam(required = false) String regime
//    ) {
//        if (timeframe == null || timeframe.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(service.getMarketStructure(timeframe.trim(), benchmark, dateSk, regime));
//    }
//
//    // Sector Performance endpoint
//    @GetMapping("/sector-performance")
//    public ResponseEntity<List<Map<String, Object>>> sectorPerformance(
//            @RequestParam String timeframe,
//            @RequestParam(required = false) String sectorCode
//    ) {
//        if (timeframe == null || timeframe.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(service.getSectorPerformance(timeframe.trim(), sectorCode));
//    }
//}
