package com.growz.analytics.config;

import com.growz.analytics.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataLoader {

    private final ExcelImportService excelImportService;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Try .xlsb first, then .xlsx
            String filePath = null;
            File xlsbFile = new File("sales_data.xlsb");
            File xlsxFile = new File("sales_data.xlsx");
            
            if (xlsbFile.exists()) {
                filePath = "sales_data.xlsb";
                log.info("Found Excel Binary file (.xlsb)");
            } else if (xlsxFile.exists()) {
                filePath = "sales_data.xlsx";
                log.info("Found Excel file (.xlsx)");
            }
            
            if (filePath != null) {
                String sheetName = "Sales 2022 Onwards";
                log.info("Starting import from: {}", filePath);
                try {
                    int count = excelImportService.importSalesData(filePath, sheetName);
                    log.info("Successfully imported {} sales transactions", count);
                } catch (Exception e) {
                    log.error("Error importing data: {}", e.getMessage(), e);
                }
            } else {
                log.warn("Excel file not found. Looking for sales_data.xlsx or sales_data.xlsb");
                log.warn("Please place the file in the backend directory");
            }
        };
    }
}
