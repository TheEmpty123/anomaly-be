package com.mobile.backendjava.dm.repository.jpa;

import com.mobile.backendjava.dm.entities.IndexValuationDaily;
import com.mobile.backendjava.dm.entities.IndexValuationDailyId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IndexValuationDailyRepository extends JpaRepository<IndexValuationDaily, IndexValuationDailyId> {

    @Query("""
            select v
            from IndexValuationDaily v
            join fetch v.symbol s
            join fetch v.date d
            where upper(s.symbol) = :symbol
              and (:startDate is null or d.fullDate >= :startDate)
              and (:endDate is null or d.fullDate <= :endDate)
            order by d.fullDate asc
            """)
    List<IndexValuationDaily> findHistoricalBySymbol(
            @Param("symbol") String symbol,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            select v
            from IndexValuationDaily v
            join fetch v.symbol s
            join fetch v.date d
            where upper(s.symbol) = :symbol
            order by d.fullDate desc
            """)
    List<IndexValuationDaily> findLatestBySymbol(@Param("symbol") String symbol, Pageable pageable);
}
