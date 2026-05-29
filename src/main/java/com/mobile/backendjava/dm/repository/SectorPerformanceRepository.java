package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.SectorPerformance;
import com.mobile.backendjava.dm.entities.SectorPerformanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SectorPerformanceRepository extends JpaRepository<SectorPerformance, SectorPerformanceId> {

    @Query("SELECT MAX(s.ingestionTime) FROM SectorPerformance s WHERE s.id.timeframe = :timeframe")
    LocalDateTime findMaxIngestionTimeByTimeframe(@Param("timeframe") String timeframe);

    List<SectorPerformance> findAllByIdTimeframeAndIngestionTimeOrderByIdSectorCode(String timeframe, LocalDateTime ingestionTime);

    List<SectorPerformance> findAllByIdTimeframeAndIdSectorCodeAndIngestionTime(String timeframe, String sectorCode, LocalDateTime ingestionTime);
}
