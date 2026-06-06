package com.mobile.backendjava.dm.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This file is a template, DO NOT MODIFY OR USE IT
 */
@Repository
public class DataMartRepository {

    private final JdbcTemplate jdbcTemplate;

    public DataMartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int ping() {
        return jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }

    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        String currentDb = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        String currentUser = jdbcTemplate.queryForObject("SELECT current_user", String.class);
        String now = jdbcTemplate.queryForObject("SELECT NOW()::text", String.class);
        result.put("version", version);
        result.put("database", currentDb);
        result.put("user", currentUser);
        result.put("now", now);
        return result;
    }

    // ===== RRG =====
    public List<Map<String, Object>> getSectorRRG(String regime, String benchmark, Integer dateSk) {
        Integer effectiveDate = dateSk;
        if (effectiveDate == null) {
            effectiveDate = jdbcTemplate.queryForObject(
                    "SELECT MAX(date_sk) FROM stellar_dm.sector_rrg_cache WHERE regime = ? AND benchmark = ?",
                    Integer.class, regime, benchmark
            );
        }
        if (effectiveDate == null) {
            return List.of();
        }
        String sql = "SELECT sector_code, date_sk, rs, rm, phase, stock_count, total_stocks, sector_name, sector_name_en, block_type, top_stocks_by_cap::text AS top_stocks_by_cap, benchmark, ingestion_time, regime " +
                "FROM stellar_dm.sector_rrg_cache WHERE regime = ? AND benchmark = ? AND date_sk = ? ORDER BY sector_code";
        return jdbcTemplate.queryForList(sql, regime, benchmark, effectiveDate);
    }

    // ===== Market Structure =====
    public Map<String, Object> getMarketStructure(String timeframe, String benchmark, Integer dateSk, String regime) {
        Integer effectiveDate = dateSk;
        if (effectiveDate == null) {
            Integer fromRrg = null;
            if (regime != null && !regime.isBlank()) {
                try {
                    fromRrg = jdbcTemplate.queryForObject(
                            "SELECT MAX(date_sk) FROM stellar_dm.sector_rrg_cache WHERE regime = ? AND benchmark = ?",
                            Integer.class, regime.trim(), benchmark
                    );
                } catch (Exception ignored) { }
            }
            if (fromRrg != null) {
                effectiveDate = fromRrg;
            } else {
                effectiveDate = jdbcTemplate.queryForObject(
                        "SELECT MAX(date_sk) FROM stellar_dm.market_structure_cache WHERE timeframe = ? AND benchmark = ?",
                        Integer.class, timeframe, benchmark
                );
            }
        }
        if (effectiveDate == null) {
            return Map.of();
        }
        String sql = "SELECT date_sk, timeframe, benchmark, market_structure_code, market_structure_label, " +
                "core_sectors::text AS core_sectors, core_blocks::text AS core_blocks, top_ecosystem_code, top_ecosystem_name, " +
                "sector_rankings::text AS sector_rankings, ecosystem_rankings::text AS ecosystem_rankings, ingestion_time " +
                "FROM stellar_dm.market_structure_cache WHERE timeframe = ? AND benchmark = ? AND date_sk = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, timeframe, benchmark, effectiveDate);
        return list.isEmpty() ? Map.of() : list.get(0);
    }

    // ===== Sector Performance Chart =====
    public List<Map<String, Object>> getSectorPerformance(String timeframe, String sectorCode) {
        String baseSql = "SELECT sector_code, block_type, timeframe, chart_data::text AS chart_data, ingestion_time " +
                "FROM stellar_dm.sector_performance_chart_cache WHERE timeframe = ?";
        if (sectorCode == null || sectorCode.isBlank()) {
            return jdbcTemplate.queryForList(baseSql + " ORDER BY sector_code", timeframe);
        } else {
            return jdbcTemplate.queryForList(baseSql + " AND sector_code = ?", timeframe, sectorCode);
        }
    }
}
