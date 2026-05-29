package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.SectorRrgCache;
import com.mobile.backendjava.dm.entities.SectorRrgCacheId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RRGRepository extends JpaRepository<SectorRrgCache, SectorRrgCacheId> {

    @Query("SELECT MAX(s.id.dateSk) FROM SectorRrgCache s WHERE s.id.regime = :regime AND s.id.benchmark = :benchmark")
    Integer findMaxDateSkByRegimeAndBenchmark(@Param("regime") String regime,
                                              @Param("benchmark") String benchmark);

    List<SectorRrgCache> findAllByIdRegimeAndIdBenchmarkAndIdDateSkOrderByIdSectorCode(String regime,
                                                                                       String benchmark,
                                                                                       Integer dateSk);
}
