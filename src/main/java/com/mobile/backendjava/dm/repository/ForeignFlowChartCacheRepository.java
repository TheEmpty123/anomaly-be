package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.ForeignFlowChartCache;
import com.mobile.backendjava.dm.entities.ForeignFlowChartCacheId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForeignFlowChartCacheRepository extends JpaRepository<ForeignFlowChartCache, ForeignFlowChartCacheId> {

    @Query("""
            select c
            from ForeignFlowChartCache c
            where c.id.entityType = :entityType
              and c.id.entityCode = :entityCode
              and c.id.timeframe = :timeframe
              and (:fromDateSk is null or c.id.dateSk >= :fromDateSk)
              and (:toDateSk is null or c.id.dateSk <= :toDateSk)
            """)
    List<ForeignFlowChartCache> findChart(
            @Param("entityType") String entityType,
            @Param("entityCode") String entityCode,
            @Param("timeframe") String timeframe,
            @Param("fromDateSk") Integer fromDateSk,
            @Param("toDateSk") Integer toDateSk,
            Pageable pageable);
}
