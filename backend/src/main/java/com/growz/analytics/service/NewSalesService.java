package com.growz.analytics.service;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.repository.SalesTransactionRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewSalesService {

    private final SalesTransactionRepository salesTransactionRepository;

    @Cacheable(value = "salesByBrand", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<MetricDTO> getSalesByBrand(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching sales by brand from {} to {}", startDate, endDate);
        return salesTransactionRepository.getSalesByBrand(startDate, endDate);
    }

    @Cacheable(value = "salesByCategory", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<MetricDTO> getSalesByCategory(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching sales by category from {} to {}", startDate, endDate);
        return salesTransactionRepository.getSalesByCategory(startDate, endDate);
    }

    @Cacheable(value = "salesByRegion", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<MetricDTO> getSalesByRegion(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching sales by region from {} to {}", startDate, endDate);
        return salesTransactionRepository.getSalesByCountry(startDate, endDate);
    }

    // FIXED: Changed tuple.get() to use new column aliases (yr, mon, val) instead of reserved keywords (year, month, value)
    @Cacheable(value = "salesByMonth", key = "#startDate.toString() + '-' + #endDate.toString()")
    public List<TimeSeriesDTO> getSalesByMonth(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching sales by month from {} to {}", startDate, endDate);
        List<Tuple> rawResults = salesTransactionRepository.getSalesByMonthRaw(startDate, endDate);
        return rawResults.stream()
                .map(tuple -> new TimeSeriesDTO(
                        tuple.get("yr", Integer.class),
                        tuple.get("mon", Integer.class),
                        tuple.get("val", BigDecimal.class)
                ))
                .toList();
    }

    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching total sales from {} to {}", startDate, endDate);
        return salesTransactionRepository.getTotalSales(startDate, endDate);
    }

    public List<MetricDTO> getTopProducts(LocalDate startDate, LocalDate endDate, int limit) {
        log.debug("Fetching top {} products from {} to {}", limit, startDate, endDate);
        return salesTransactionRepository.getTopProducts(startDate, endDate, PageRequest.of(0, limit)).getContent();
    }

    public YoYComparisonDTO getYoYComparison(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating YoY comparison from {} to {}", startDate, endDate);
        
        BigDecimal currentValue = getTotalSales(startDate, endDate);
        
        LocalDate previousStartDate = startDate.minusYears(1);
        LocalDate previousEndDate = endDate.minusYears(1);
        BigDecimal previousValue = getTotalSales(previousStartDate, previousEndDate);
        
        BigDecimal percentageChange = null;
        if (previousValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = currentValue.subtract(previousValue);
            percentageChange = change.divide(previousValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        return new YoYComparisonDTO(
                currentValue,
                previousValue,
                percentageChange
        );
    }

    // Alias methods for backward compatibility
    public YoYComparisonDTO calculateYoYSalesComparison(LocalDate startDate, LocalDate endDate) {
        return getYoYComparison(startDate, endDate);
    }

    public List<MetricDTO> getSalesByProduct(LocalDate startDate, LocalDate endDate, org.springframework.data.domain.Pageable pageable) {
        return getTopProducts(startDate, endDate, pageable.getPageSize());
    }
}
