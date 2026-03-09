package com.growz.analytics.service;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.repository.SalesTransactionRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewStoreService {

    private final SalesTransactionRepository salesTransactionRepository;

    @Cacheable(value = "activeStores", key = "#startDate.toString() + '-' + #endDate.toString()")
    public Long getActiveStoreCount(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching active store count from {} to {}", startDate, endDate);
        // OLD LOGIC: Count distinct customers across entire date range
        // return salesTransactionRepository.getActiveStoreCount(startDate, endDate);
        
        // NEW LOGIC: Return latest month's active store count (matches Excel logic)
        // Get monthly active stores and return the last month's value
        List<TimeSeriesDTO> monthlyActiveStores = getActiveStoresByMonth(startDate, endDate);
        if (monthlyActiveStores.isEmpty()) {
            return 0L;
        }
        // Return the last month's active store count
        return monthlyActiveStores.get(monthlyActiveStores.size() - 1).getValue().longValue();
    }

    @Cacheable(value = "activeStoresByBrand", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<MetricDTO> getActiveStoresByBrand(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching active stores by brand from {} to {}", startDate, endDate);
        return salesTransactionRepository.getActiveStoresByBrand(startDate, endDate);
    }

    @Cacheable(value = "activeStoresByRegion", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<MetricDTO> getActiveStoresByRegion(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching active stores by region from {} to {}", startDate, endDate);
        return salesTransactionRepository.getActiveStoresByCountry(startDate, endDate);
    }

    // FIXED: Changed tuple.get() to use new column aliases (yr, mon, val) instead of reserved keywords (year, month, value)
    @Cacheable(value = "activeStoresByMonth", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<TimeSeriesDTO> getActiveStoresByMonth(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching active stores by month from {} to {}", startDate, endDate);
        List<Tuple> rawResults = salesTransactionRepository.getActiveStoresByMonthRaw(startDate, endDate);
        return rawResults.stream()
                .map(tuple -> new TimeSeriesDTO(
                        tuple.get("yr", Integer.class),
                        tuple.get("mon", Integer.class),
                        BigDecimal.valueOf(tuple.get("val", Double.class))
                ))
                .toList();
    }

    public YoYComparisonDTO getYoYComparison(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating YoY comparison for active stores from {} to {}", startDate, endDate);
        
        Long currentValue = getActiveStoreCount(startDate, endDate);
        
        LocalDate previousStartDate = startDate.minusYears(1);
        LocalDate previousEndDate = endDate.minusYears(1);
        Long previousValue = getActiveStoreCount(previousStartDate, previousEndDate);
        
        BigDecimal percentageChange = null;
        if (previousValue > 0) {
            BigDecimal change = BigDecimal.valueOf(currentValue - previousValue);
            percentageChange = change.divide(BigDecimal.valueOf(previousValue), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        return new YoYComparisonDTO(
                BigDecimal.valueOf(currentValue),
                BigDecimal.valueOf(previousValue),
                percentageChange
        );
    }

    // Alias methods for backward compatibility
    public Long countActiveStores(LocalDate startDate, LocalDate endDate) {
        return getActiveStoreCount(startDate, endDate);
    }

    public List<MetricDTO> countActiveStoresByBrand(LocalDate startDate, LocalDate endDate) {
        return getActiveStoresByBrand(startDate, endDate);
    }

    public List<MetricDTO> countActiveStoresByRegion(LocalDate startDate, LocalDate endDate) {
        return getActiveStoresByRegion(startDate, endDate);
    }

    public List<TimeSeriesDTO> countActiveStoresByMonth(LocalDate startDate, LocalDate endDate) {
        return getActiveStoresByMonth(startDate, endDate);
    }

    public YoYComparisonDTO calculateYoYActiveStoresComparison(LocalDate startDate, LocalDate endDate) {
        return getYoYComparison(startDate, endDate);
    }
}
