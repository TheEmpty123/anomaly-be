package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.MarketStructureCache;
import com.mobile.backendjava.dm.entities.MarketStructureCacheId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketStructureRepository extends JpaRepository<MarketStructureCache, MarketStructureCacheId> {

    @Query("SELECT MAX(m.id.dateSk) FROM MarketStructureCache m WHERE m.id.timeframe = :timeframe AND m.id.benchmark = :benchmark")
    Integer findMaxDateSkByTimeframeAndBenchmark(@Param("timeframe") String timeframe,
                                                 @Param("benchmark") String benchmark);

    Optional<MarketStructureCache> findByIdDateSkAndIdTimeframeAndIdBenchmark(Integer dateSk,
                                                                              String timeframe,
                                                                              String benchmark);
}
