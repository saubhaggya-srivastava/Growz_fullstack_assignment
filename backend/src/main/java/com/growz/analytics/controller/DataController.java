package com.growz.analytics.controller;

import com.growz.analytics.dto.IngestionResultDTO;
import com.growz.analytics.service.DataIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for data ingestion endpoints.
 * 
 * Provides APIs for uploading CSV and Excel files.
 */
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "Data Ingestion", description = "Data upload and ingestion APIs")
public class DataController {

    private final DataIngestionService dataIngestionService;

    @PostMapping("/upload/csv")
    @Operation(summary = "Upload CSV file", description = "Upload and ingest data from CSV file. Filename should contain 'product', 'region', 'store', or 'sales' to determine entity type.")
    public ResponseEntity<IngestionResultDTO> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().build();
            }
            
            IngestionResultDTO result = dataIngestionService.ingestFromCSV(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload/excel")
    @Operation(summary = "Upload Excel file", description = "Upload and ingest data from Excel file (future implementation)")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        // Excel parsing can be implemented similarly to CSV
        // For now, return not implemented
        return ResponseEntity.status(501).body("Excel upload not yet implemented");
    }
}
