package com.growz.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for metric data with name and value.
 * Used for aggregated results like sales by brand, region, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {
    private String name;
    private BigDecimal value;
}
