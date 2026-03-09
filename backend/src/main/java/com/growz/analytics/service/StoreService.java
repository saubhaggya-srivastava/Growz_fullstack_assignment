package com.growz.analytics.service;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.repository.SalesRepository;
import com.growz.analytics.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for active store analytics operations.
 * 
 * Implements business logic for active store metrics and YoY comparisons.
 * An active store is defined as a store with at least one sale in the period.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final SalesRepository salesRepository;

    /**
     * Count total active stores for a date range.
     */
    public Long countActiveStores(LocalDate startDate, LocalDate endDate) {
        Long count = salesRepository.countActiveStores(startDate, endDate);
        return count != null ? count : 0L;
    }

    /**
     * Count active stores by brand for a date range.
     * Results are cached to improve performance.
     */
    @Cacheable(value = "activeStoresByBrand")
    public List<MetricDTO> countActiveStoresByBrand(LocalDate startDate, LocalDate endDate) {
        return storeRepository.countActiveStoresByBrand(startDate, endDate);
    }

    /**
     * Count active stores by region for a date range.
     * Results are cached to improve performance.
     */
    @Cacheable(value = "activeStoresByRegion")
    public List<MetricDTO> countActiveStoresByRegion(LocalDate startDate, LocalDate endDate) {
        return storeRepository.countActiveStoresByRegion(startDate, endDate);
    }

    /**
     * Count active stores by month for a date range.
     * Results are cached to improve performance.
     */
    @Cacheable(value = "activeStores")
    public List<TimeSeriesDTO> countActiveStoresByMonth(LocalDate startDate, LocalDate endDate) {
        return storeRepository.countActiveStoresByMonth(startDate, endDate);
    }

    /**
     * Calculate Year-over-Year active stores comparison.
     * Compares current period with same period in previous year.
     */
    public YoYComparisonDTO calculateYoYActiveStoresComparison(LocalDate currentStart, LocalDate currentEnd) {
        LocalDate previousStart = currentStart.minusYears(1);
        LocalDate previousEnd = currentEnd.minusYears(1);

        Long currentCount = countActiveStores(currentStart, currentEnd);
        Long previousCount = countActiveStores(previousStart, previousEnd);

        BigDecimal current = BigDecimal.valueOf(currentCount);
        BigDecimal previous = BigDecimal.valueOf(previousCount);

        return calculatePercentageChange(current, previous);
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
