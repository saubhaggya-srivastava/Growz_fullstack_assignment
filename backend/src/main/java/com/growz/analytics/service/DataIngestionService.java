package com.growz.analytics.service;

import com.growz.analytics.dto.IngestionResultDTO;
import com.growz.analytics.entity.Product;
import com.growz.analytics.entity.Region;
import com.growz.analytics.entity.Sales;
import com.growz.analytics.entity.Store;
import com.growz.analytics.repository.ProductRepository;
import com.growz.analytics.repository.RegionRepository;
import com.growz.analytics.repository.SalesRepository;
import com.growz.analytics.repository.StoreRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for ingesting data from CSV and Excel files.
 * 
 * Handles parsing and persisting Product, Region, Store, and Sales entities.
 * Implements error handling at row level to continue processing valid records.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DataIngestionService {

    private final ProductRepository productRepository;
    private final RegionRepository regionRepository;
    private final StoreRepository storeRepository;
    private final SalesRepository salesRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Ingest data from CSV file.
     * Routes to appropriate entity-specific method based on filename.
     */
    @CacheEvict(value = {"salesByBrand", "salesByProduct", "salesByMonth", "salesByRegion", 
                         "salesByCategory", "activeStores", "activeStoresByBrand", "activeStoresByRegion"}, 
                allEntries = true)
    public IngestionResultDTO ingestFromCSV(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        List<String[]> rows = parseCSV(file);
        
        if (filename.toLowerCase().contains("product")) {
            return ingestProducts(rows);
        } else if (filename.toLowerCase().contains("region")) {
            return ingestRegions(rows);
        } else if (filename.toLowerCase().contains("store")) {
            return ingestStores(rows);
        } else if (filename.toLowerCase().contains("sales")) {
            return ingestSales(rows);
        }

        throw new IllegalArgumentException("Unknown file type: " + filename);
    }

    /**
     * Parse CSV file into list of string arrays.
     */
    private List<String[]> parseCSV(MultipartFile file) throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            // Skip header row
            if (!rows.isEmpty()) {
                rows.remove(0);
            }
            return rows;
        } catch (CsvException e) {
            throw new IOException("Error parsing CSV file", e);
        }
    }

    /**
     * Ingest products from parsed CSV rows.
     */
    private IngestionResultDTO ingestProducts(List<String[]> rows) {
        int success = 0;
        int errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            try {
                String[] row = rows.get(i);
                if (row.length < 4) {
                    throw new IllegalArgumentException("Insufficient columns");
                }

                Product product = new Product();
                product.setProductCode(row[0].trim());
                product.setName(row[1].trim());
                product.setBrand(row[2].trim());
                product.setCategory(row[3].trim());
                
                productRepository.save(product);
                success++;
            } catch (Exception e) {
                errors++;
                errorMessages.add("Row " + (i + 2) + ": " + e.getMessage());
                log.warn("Error ingesting product at row {}: {}", i + 2, e.getMessage());
            }
        }

        return new IngestionResultDTO(success, errors, errorMessages);
    }

    /**
     * Ingest regions from parsed CSV rows.
     */
    private IngestionResultDTO ingestRegions(List<String[]> rows) {
        int success = 0;
        int errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            try {
                String[] row = rows.get(i);
                if (row.length < 2) {
                    throw new IllegalArgumentException("Insufficient columns");
                }

                Region region = new Region();
                region.setRegionCode(row[0].trim());
                region.setName(row[1].trim());
                
                regionRepository.save(region);
                success++;
            } catch (Exception e) {
                errors++;
                errorMessages.add("Row " + (i + 2) + ": " + e.getMessage());
                log.warn("Error ingesting region at row {}: {}", i + 2, e.getMessage());
            }
        }

        return new IngestionResultDTO(success, errors, errorMessages);
    }

    /**
     * Ingest stores from parsed CSV rows.
     */
    private IngestionResultDTO ingestStores(List<String[]> rows) {
        int success = 0;
        int errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            try {
                String[] row = rows.get(i);
                if (row.length < 3) {
                    throw new IllegalArgumentException("Insufficient columns");
                }

                Store store = new Store();
                store.setStoreCode(row[0].trim());
                store.setName(row[1].trim());
                
                // Lookup region by code
                String regionCode = row[2].trim();
                Region region = regionRepository.findAll().stream()
                    .filter(r -> r.getRegionCode().equals(regionCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Region not found: " + regionCode));
                
                store.setRegion(region);
                storeRepository.save(store);
                success++;
            } catch (Exception e) {
                errors++;
                errorMessages.add("Row " + (i + 2) + ": " + e.getMessage());
                log.warn("Error ingesting store at row {}: {}", i + 2, e.getMessage());
            }
        }

        return new IngestionResultDTO(success, errors, errorMessages);
    }

    /**
     * Ingest sales from parsed CSV rows.
     */
    private IngestionResultDTO ingestSales(List<String[]> rows) {
        int success = 0;
        int errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            try {
                String[] row = rows.get(i);
                if (row.length < 5) {
                    throw new IllegalArgumentException("Insufficient columns");
                }

                Sales sales = new Sales();
                
                // Lookup product by code
                String productCode = row[0].trim();
                Product product = productRepository.findAll().stream()
                    .filter(p -> p.getProductCode().equals(productCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productCode));
                
                // Lookup store by code
                String storeCode = row[1].trim();
                Store store = storeRepository.findAll().stream()
                    .filter(s -> s.getStoreCode().equals(storeCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Store not found: " + storeCode));
                
                sales.setProduct(product);
                sales.setStore(store);
                sales.setInvoiceDate(LocalDate.parse(row[2].trim(), DATE_FORMATTER));
                sales.setQuantity(Integer.parseInt(row[3].trim()));
                sales.setAmount(new BigDecimal(row[4].trim()));
                
                salesRepository.save(sales);
                success++;
            } catch (Exception e) {
                errors++;
                errorMessages.add("Row " + (i + 2) + ": " + e.getMessage());
                log.warn("Error ingesting sales at row {}: {}", i + 2, e.getMessage());
            }
        }

        return new IngestionResultDTO(success, errors, errorMessages);
    }
}
