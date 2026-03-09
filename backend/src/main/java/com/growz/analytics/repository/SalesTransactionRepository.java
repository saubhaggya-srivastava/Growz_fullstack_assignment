package com.growz.analytics.repository;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.entity.SalesTransaction;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {

    // Total Sales
    @Query("SELECT COALESCE(SUM(s.value), 0) FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSales(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // Sales by Brand
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.brand, SUM(s.value)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.brand " +
           "ORDER BY SUM(s.value) DESC")
    List<MetricDTO> getSalesByBrand(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    // Sales by Category
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.category, SUM(s.value)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.category " +
           "ORDER BY SUM(s.value) DESC")
    List<MetricDTO> getSalesByCategory(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Sales by Country (Region)
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.country, SUM(s.value)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.country " +
           "ORDER BY SUM(s.value) DESC")
    List<MetricDTO> getSalesByCountry(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

     // Sales by Month - Native query with proper escaping
    @Query(value = "SELECT \"year\" as yr, " +
           "CASE \"month\" " +
           "WHEN 'JAN' THEN 1 WHEN 'FEB' THEN 2 WHEN 'MAR' THEN 3 " +
           "WHEN 'APR' THEN 4 WHEN 'MAY' THEN 5 WHEN 'JUN' THEN 6 " +
           "WHEN 'JUL' THEN 7 WHEN 'AUG' THEN 8 WHEN 'SEP' THEN 9 " +
           "WHEN 'OCT' THEN 10 WHEN 'NOV' THEN 11 WHEN 'DEC' THEN 12 " +
           "ELSE 0 END as mon, " +
           "SUM(\"value\") as val " +
           "FROM sales_transactions " +
           "WHERE invoice_date BETWEEN :startDate AND :endDate " +
           "GROUP BY \"year\", \"month\" " +
           "ORDER BY \"year\", " +
           "CASE \"month\" " +
           "WHEN 'JAN' THEN 1 WHEN 'FEB' THEN 2 WHEN 'MAR' THEN 3 " +
           "WHEN 'APR' THEN 4 WHEN 'MAY' THEN 5 WHEN 'JUN' THEN 6 " +
           "WHEN 'JUL' THEN 7 WHEN 'AUG' THEN 8 WHEN 'SEP' THEN 9 " +
           "WHEN 'OCT' THEN 10 WHEN 'NOV' THEN 11 WHEN 'DEC' THEN 12 " +
           "ELSE 0 END", nativeQuery = true)
    List<Tuple> getSalesByMonthRaw(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Top Products
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.itemDescription, SUM(s.value)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.itemDescription " +
           "ORDER BY SUM(s.value) DESC")
    Page<MetricDTO> getTopProducts(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    Pageable pageable);

    // Active Stores Count (distinct customers with invoices)
    // FIXED: Use customer_account_name (actual store name) instead of customer
    @Query("SELECT COUNT(DISTINCT s.customerAccountName) FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate AND s.value > 0")
    Long getActiveStoreCount(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // Active Stores by Brand
    // FIXED: Use customer_account_name (actual store name) instead of customer
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.brand, CAST(CAST(COUNT(DISTINCT s.customerAccountName) AS long) AS java.math.BigDecimal)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate AND s.value > 0 " +
           "GROUP BY s.brand " +
           "ORDER BY COUNT(DISTINCT s.customerAccountName) DESC")
    List<MetricDTO> getActiveStoresByBrand(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // Active Stores by Country (Region)
    // FIXED: Use customer_account_name (actual store name) instead of customer
    @Query("SELECT new com.growz.analytics.dto.MetricDTO(s.country, CAST(CAST(COUNT(DISTINCT s.customerAccountName) AS long) AS java.math.BigDecimal)) " +
           "FROM SalesTransaction s " +
           "WHERE s.invoiceDate BETWEEN :startDate AND :endDate AND s.value > 0 " +
           "GROUP BY s.country " +
           "ORDER BY COUNT(DISTINCT s.customerAccountName) DESC")
    List<MetricDTO> getActiveStoresByCountry(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Active Stores by Month - Native query with proper escaping
    // Matches Excel COUNTIF(>0) logic: Count stores with SUM(sales) > 0 per month
    // FIXED: Use customer_account_name (actual store name) instead of customer
    @Query(value = "SELECT sub.yr as yr, sub.mon as mon, CAST(COUNT(*) AS double) as val " +
           "FROM (" +
           "  SELECT \"year\" as yr, " +
           "  CASE \"month\" " +
           "  WHEN 'JAN' THEN 1 WHEN 'FEB' THEN 2 WHEN 'MAR' THEN 3 " +
           "  WHEN 'APR' THEN 4 WHEN 'MAY' THEN 5 WHEN 'JUN' THEN 6 " +
           "  WHEN 'JUL' THEN 7 WHEN 'AUG' THEN 8 WHEN 'SEP' THEN 9 " +
           "  WHEN 'OCT' THEN 10 WHEN 'NOV' THEN 11 WHEN 'DEC' THEN 12 " +
           "  ELSE 0 END as mon, " +
           "  customer_account_name, SUM(\"value\") as total_sales " +
           "  FROM sales_transactions " +
           "  WHERE invoice_date BETWEEN :startDate AND :endDate " +
           "  GROUP BY \"year\", \"month\", customer_account_name " +
           "  HAVING SUM(\"value\") > 0" +
           ") sub " +
           "GROUP BY sub.yr, sub.mon " +
           "ORDER BY sub.yr, sub.mon", nativeQuery = true)
    List<Tuple> getActiveStoresByMonthRaw(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
