package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.ForeignFlowHeatmapCache;
import com.mobile.backendjava.dm.entities.ForeignFlowHeatmapCacheId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForeignFlowHeatmapCacheRepository extends JpaRepository<ForeignFlowHeatmapCache, ForeignFlowHeatmapCacheId> {

    @Query("""
            select h
            from ForeignFlowHeatmapCache h
            where h.id.dateSk = :dateSk
              and h.id.timeframe = :timeframe
              and (:direction is null or h.direction = :direction)
            """)
    List<ForeignFlowHeatmapCache> findHeatmap(
            @Param("dateSk") Integer dateSk,
            @Param("timeframe") String timeframe,
            @Param("direction") String direction,
            Pageable pageable);

    @Query("""
            select h
            from ForeignFlowHeatmapCache h
            where h.id.dateSk = :dateSk
              and h.id.timeframe = :timeframe
              and (:direction is null or h.direction = :direction)
            order by abs(h.cumulativeNetVal) desc
            """)
    List<ForeignFlowHeatmapCache> findHeatmapOrderByAbsNetVal(
            @Param("dateSk") Integer dateSk,
            @Param("timeframe") String timeframe,
            @Param("direction") String direction,
            Pageable pageable);
}
