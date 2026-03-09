package com.growz.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for data ingestion results.
 * Contains counts of successful and failed records, plus error messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngestionResultDTO {
    private Integer successCount;
    private Integer errorCount;
    private List<String> errorMessages = new ArrayList<>();
    
    public IngestionResultDTO(Integer successCount, Integer errorCount) {
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.errorMessages = new ArrayList<>();
    }
}
