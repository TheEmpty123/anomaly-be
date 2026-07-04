package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.entities.DimSymbol;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DimSymbolRepository extends JpaRepository<DimSymbol, Integer> {

    @Query("""
            select s
            from DimSymbol s
            where (:activeOnly = false or (s.isActive = true and s.isCurrent = 1))
            """)
    List<DimSymbol> findSymbols(@Param("activeOnly") boolean activeOnly, Pageable pageable);
}
