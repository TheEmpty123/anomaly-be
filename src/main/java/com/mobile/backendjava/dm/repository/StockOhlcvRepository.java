package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.StockOhlcv;
import com.mobile.backendjava.dm.entities.StockOhlcvId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOhlcvRepository extends JpaRepository<StockOhlcv, StockOhlcvId> {

    @Query(value = """
            select distinct s.symbol
            from stellar_dm.stock_ohlcv as o
            join stellar_dm.dim_symbol as s on s.symbol_id = o.symbol_sk
            order by s.symbol
            """, nativeQuery = true)
    List<String> findAvailableSymbols();

    @Query("""
            select o
            from StockOhlcv o
            join fetch o.symbol s
            join fetch o.date d
            where upper(s.symbol) = :symbol
              and lower(o.id.timeframe) = :timeframe
              and (:fromDateSk is null or o.id.dateSk >= :fromDateSk)
              and (:toDateSk is null or o.id.dateSk <= :toDateSk)
            """)
    List<StockOhlcv> findBySymbol(
            @Param("symbol") String symbol,
            @Param("timeframe") String timeframe,
            @Param("fromDateSk") Integer fromDateSk,
            @Param("toDateSk") Integer toDateSk,
            Pageable pageable);

    @Query("""
            select o
            from StockOhlcv o
            join fetch o.symbol s
            join fetch o.date d
            where o.id.dateSk = :dateSk
              and lower(o.id.timeframe) = :timeframe
            """)
    List<StockOhlcv> findMarketSnapshot(
            @Param("dateSk") Integer dateSk,
            @Param("timeframe") String timeframe,
            Pageable pageable);

    @Query("select max(o.id.dateSk) from StockOhlcv o where lower(o.id.timeframe) = :timeframe")
    Integer findMaxDateSkByTimeframe(@Param("timeframe") String timeframe);
}
