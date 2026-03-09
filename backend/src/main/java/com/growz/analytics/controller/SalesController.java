package com.growz.analytics.controller;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.service.NewSalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for sales analytics endpoints.
 * 
 * Provides APIs for sales metrics, aggregations, and YoY comparisons.
 */
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales analytics APIs")
public class SalesController {

    private final NewSalesService salesService;

    @GetMapping("/by-brand")
    @Operation(summary = "Get sales by brand", description = "Returns total sales amount aggregated by brand for the specified date range")
    public ResponseEntity<List<MetricDTO>> getSalesByBrand(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getSalesByBrand(startDate, endDate));
    }

    @GetMapping("/by-product")
    @Operation(summary = "Get sales by product with pagination", description = "Returns total sales amount aggregated by product with pagination support")
    public ResponseEntity<List<MetricDTO>> getSalesByProduct(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getTopProducts(startDate, endDate, size));
    }

    @GetMapping("/by-month")
    @Operation(summary = "Get sales trend by month", description = "Returns sales time series aggregated by month")
    public ResponseEntity<List<TimeSeriesDTO>> getSalesByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getSalesByMonth(startDate, endDate));
    }

    @GetMapping("/by-region")
    @Operation(summary = "Get sales by region", description = "Returns total sales amount aggregated by region")
    public ResponseEntity<List<MetricDTO>> getSalesByRegion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getSalesByRegion(startDate, endDate));
    }

    @GetMapping("/by-category")
    @Operation(summary = "Get sales by category", description = "Returns total sales amount aggregated by product category")
    public ResponseEntity<List<MetricDTO>> getSalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getSalesByCategory(startDate, endDate));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top N products by sales", description = "Returns top products by sales amount with pagination")
    public ResponseEntity<List<MetricDTO>> getTopProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getTopProducts(startDate, endDate, limit));
    }

    @GetMapping("/total")
    @Operation(summary = "Get total sales", description = "Returns total sales amount for the specified date range")
    public ResponseEntity<BigDecimal> getTotalSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.getTotalSales(startDate, endDate));
    }

    @GetMapping("/yoy-comparison")
    @Operation(summary = "Get year-over-year sales comparison", description = "Returns YoY comparison with current value, previous value, and percentage change")
    public ResponseEntity<YoYComparisonDTO> getYoYComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(salesService.calculateYoYSalesComparison(startDate, endDate));
    }
}
