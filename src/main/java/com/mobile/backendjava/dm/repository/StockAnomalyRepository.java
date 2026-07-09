package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.StockAnomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockAnomalyRepository extends JpaRepository<StockAnomaly, Long> {

    List<StockAnomaly> findByPredictionDateOrderBySymbolAscIdAsc(LocalDate predictionDate);

    @Query("select max(a.predictionDate) from StockAnomaly a")
    LocalDate findLatestPredictionDate();
}
