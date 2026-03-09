package com.growz.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Year-over-Year comparison data.
 * Contains current value, previous year value, and percentage change.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoYComparisonDTO {
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private BigDecimal percentageChange;  // null if previous value is zero
}
