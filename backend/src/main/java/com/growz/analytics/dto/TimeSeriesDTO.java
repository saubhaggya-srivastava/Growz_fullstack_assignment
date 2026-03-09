package com.growz.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for time series data with year, month, and value.
 * Used for trend analysis over time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDTO {
    private Integer year;
    private Integer month;
    private BigDecimal value;
}
