package com.growz.analytics.repository;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Store entity.
 * Provides CRUD operations and custom queries for active store analytics.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * Count active stores by brand for a given date range.
     * An active store is one that has at least one sale in the period.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(p.brand, CAST(COUNT(DISTINCT s.store.id) AS BigDecimal)) " +
           "FROM Sales s JOIN s.product p " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.brand")
    List<MetricDTO> countActiveStoresByBrand(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count active stores by region for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(r.name, CAST(COUNT(DISTINCT s.store.id) AS BigDecimal)) " +
           "FROM Sales s JOIN s.store st JOIN st.region r " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY r.name")
    List<MetricDTO> countActiveStoresByRegion(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count active stores by month for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.TimeSeriesDTO(" +
           "FUNCTION('YEAR', s.invoiceDate), " +
           "FUNCTION('MONTH', s.invoiceDate), " +
           "CAST(COUNT(DISTINCT s.store.id) AS BigDecimal)) " +
           "FROM Sales s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('YEAR', s.invoiceDate), FUNCTION('MONTH', s.invoiceDate) " +
           "ORDER BY FUNCTION('YEAR', s.invoiceDate), FUNCTION('MONTH', s.invoiceDate)")
    List<TimeSeriesDTO> countActiveStoresByMonth(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
