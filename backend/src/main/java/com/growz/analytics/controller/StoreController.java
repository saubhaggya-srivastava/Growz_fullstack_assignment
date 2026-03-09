package com.growz.analytics.controller;

import com.growz.analytics.dto.MetricDTO;
import com.growz.analytics.dto.TimeSeriesDTO;
import com.growz.analytics.dto.YoYComparisonDTO;
import com.growz.analytics.service.NewStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for active store analytics endpoints.
 * 
 * Provides APIs for active store metrics and YoY comparisons.
 */
@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "Stores", description = "Active store analytics APIs")
public class StoreController {

    private final NewStoreService storeService;

    @GetMapping("/active-count")
    @Operation(summary = "Get active store count", description = "Returns count of active stores for the specified date range")
    public ResponseEntity<Long> getActiveStoreCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(storeService.countActiveStores(startDate, endDate));
    }

    @GetMapping("/active-by-brand")
    @Operation(summary = "Get active stores by brand", description = "Returns active store count aggregated by brand")
    public ResponseEntity<List<MetricDTO>> getActiveStoresByBrand(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(storeService.countActiveStoresByBrand(startDate, endDate));
    }

    @GetMapping("/active-by-region")
    @Operation(summary = "Get active stores by region", description = "Returns active store count aggregated by region")
    public ResponseEntity<List<MetricDTO>> getActiveStoresByRegion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(storeService.countActiveStoresByRegion(startDate, endDate));
    }

    @GetMapping("/active-by-month")
    @Operation(summary = "Get active stores trend by month", description = "Returns active store count time series aggregated by month")
    public ResponseEntity<List<TimeSeriesDTO>> getActiveStoresByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(storeService.countActiveStoresByMonth(startDate, endDate));
    }

    @GetMapping("/yoy-comparison")
    @Operation(summary = "Get year-over-year active stores comparison", description = "Returns YoY comparison for active store count")
    public ResponseEntity<YoYComparisonDTO> getYoYComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(storeService.calculateYoYActiveStoresComparison(startDate, endDate));
    }
}
