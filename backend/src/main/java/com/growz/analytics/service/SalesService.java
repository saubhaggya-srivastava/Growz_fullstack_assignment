package com.growz.analytics.service;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for sales analytics operations.
 * 
 * Implements business logic for sales metrics, YoY comparisons, and aggregations.
 * Uses @Cacheable annotations for performance optimization.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;

    /**
     * Get sales aggregated by brand for a date range.
     * Results are cached to improve performance.
     */
    @Cacheable(value = "salesByBrand", key = "#startDate + '-' + #endDate")
    public List<MetricDTO> getSalesByBrand(LocalDate startDate, LocalDate endDate) {
        return salesRepository.findSalesByBrand(startDate, endDate);
    }

    /**
     * Get sales aggregated by product for a date range with pagination.
     * Results are cached based on date range and page parameters.
     */
    @Cacheable(value = "salesByProduct")
    public Page<MetricDTO> getSalesByProduct(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return salesRepository.findSalesByProduct(startDate, endDate, pageable);
    }

    /**
     * Get sales trend by month for a date range.
     * Results are cached to improve performance.
     */
    @Cacheable(value = "salesByMonth")
    public List<TimeSeriesDTO> getSalesByMonth(LocalDate startDate, LocalDate endDate) {
        return salesRepository.findSalesByMonth(startDate, endDate);
    }

    /**
     * Get sales aggregated by region for a date range.
     */
    @Cacheable(value = "salesByRegion")
    public List<MetricDTO> getSalesByRegion(LocalDate startDate, LocalDate endDate) {
        return salesRepository.findSalesByRegion(startDate, endDate);
    }

    /**
     * Get sales aggregated by category for a date range.
     */
    @Cacheable(value = "salesByCategory")
    public List<MetricDTO> getSalesByCategory(LocalDate startDate, LocalDate endDate) {
        return salesRepository.findSalesByCategory(startDate, endDate);
    }

    /**
     * Get top N products by sales amount for a date range.
     */
    public Page<MetricDTO> getTopProducts(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return salesRepository.findSalesByProduct(startDate, endDate, pageable);
    }

    /**
     * Get total sales amount for a date range.
     */
    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = salesRepository.findTotalSales(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Calculate Year-over-Year sales comparison.
     * Compares current period with same period in previous year.
     */
    public YoYComparisonDTO calculateYoYSalesComparison(LocalDate currentStart, LocalDate currentEnd) {
        LocalDate previousStart = currentStart.minusYears(1);
        LocalDate previousEnd = currentEnd.minusYears(1);

        BigDecimal currentSales = getTotalSales(currentStart, currentEnd);
        BigDecimal previousSales = getTotalSales(previousStart, previousEnd);

        return calculatePercentageChange(currentSales, previousSales);
    }

    /**
     * Helper method to calculate percentage change between two values.
     * Returns null for percentage change if previous value is zero.
     */
    private YoYComparisonDTO calculatePercentageChange(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return new YoYComparisonDTO(current, previous, null);
        }

        BigDecimal change = current.subtract(previous)
            .divide(previous, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));

        return new YoYComparisonDTO(current, previous, change);
    }
}
