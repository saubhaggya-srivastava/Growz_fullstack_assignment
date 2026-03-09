package com.growz.analytics.repository;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.entity.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Sales entity.
 * Provides CRUD operations and custom queries for sales analytics.
 * 
 * All queries use JPQL constructor expressions to map results directly to DTOs.
 */
@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    /**
     * Find total sales amount aggregated by brand for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(p.brand, SUM(s.amount)) " +
           "FROM Sales s JOIN s.product p " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.brand")
    List<MetricDTO> findSalesByBrand(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find total sales amount aggregated by product for a given date range.
     * Supports pagination for large result sets.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(p.name, SUM(s.amount)) " +
           "FROM Sales s JOIN s.product p " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.name " +
           "ORDER BY SUM(s.amount) DESC")
    Page<MetricDTO> findSalesByProduct(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    /**
     * Find sales trend aggregated by month for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.TimeSeriesDTO(" +
           "FUNCTION('YEAR', s.invoiceDate), " +
           "FUNCTION('MONTH', s.invoiceDate), " +
           "SUM(s.amount)) " +
           "FROM Sales s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('YEAR', s.invoiceDate), FUNCTION('MONTH', s.invoiceDate) " +
           "ORDER BY FUNCTION('YEAR', s.invoiceDate), FUNCTION('MONTH', s.invoiceDate)")
    List<TimeSeriesDTO> findSalesByMonth(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find total sales amount aggregated by region for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(r.name, SUM(s.amount)) " +
           "FROM Sales s JOIN s.store st JOIN st.region r " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY r.name")
    List<MetricDTO> findSalesByRegion(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find total sales amount aggregated by product category for a given date range.
     */
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(p.category, SUM(s.amount)) " +
           "FROM Sales s JOIN s.product p " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.category")
    List<MetricDTO> findSalesByCategory(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate total sales amount for a given date range.
     */
    @Query("SELECT SUM(s.amount) FROM Sales s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal findTotalSales(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count distinct active stores (stores with at least one sale) for a given date range.
     */
    @Query("SELECT COUNT(DISTINCT s.store.id) FROM Sales s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate")
    Long countActiveStores(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
