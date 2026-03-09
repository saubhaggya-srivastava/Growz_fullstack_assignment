package com.growz.analytics.service;

import com.growz.analytics.entity.SalesTransaction;
import com.growz.analytics.repository.SalesTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelImportService {

    private final SalesTransactionRepository salesTransactionRepository;

    @Transactional
    public int importSalesData(String filePath, String sheetName) throws IOException {
        log.info("Starting import from file: {} sheet: {}", filePath, sheetName);
        
        // Check if file is .xlsb
        if (filePath.toLowerCase().endsWith(".xlsb")) {
            throw new UnsupportedOperationException(
                "Excel Binary (.xlsb) files are not supported by Apache POI. " +
                "Please convert the file to .xlsx format: " +
                "Open in Excel → File → Save As → Excel Workbook (*.xlsx)"
            );
        }
        
        List<SalesTransaction> transactions = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            int totalRows = sheet.getLastRowNum();
            log.info("Total rows to process: {}", totalRows);

            // Skip header row (row 0)
            for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                try {
                    SalesTransaction transaction = parseRow(row);
                    if (transaction != null) {
                        transactions.add(transaction);
                        successCount++;

                        // Batch insert every 1000 records
                        if (transactions.size() >= 1000) {
                            salesTransactionRepository.saveAll(transactions);
                            log.info("Saved batch. Progress: {}/{}", rowNum, totalRows);
                            transactions.clear();
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.warn("Error parsing row {}: {}", rowNum, e.getMessage());
                }
            }

            // Save remaining records
            if (!transactions.isEmpty()) {
                salesTransactionRepository.saveAll(transactions);
                log.info("Saved final batch");
            }
        }

        log.info("Import complete. Success: {}, Errors: {}", successCount, errorCount);
        return successCount;
    }

    private SalesTransaction parseRow(Row row) {
        SalesTransaction transaction = new SalesTransaction();

        try {
            // Column mapping based on Excel structure
            transaction.setMasterDistributor(getCellValueAsString(row.getCell(0)));
            transaction.setDistributor(getCellValueAsString(row.getCell(1)));
            transaction.setLineOfBusiness(getCellValueAsString(row.getCell(2)));
            transaction.setSupplier(getCellValueAsString(row.getCell(3)));
            transaction.setAgency(getCellValueAsString(row.getCell(4)));
            transaction.setCategory(getCellValueAsString(row.getCell(5)));
            transaction.setSegment(getCellValueAsString(row.getCell(6)));
            transaction.setBrand(getCellValueAsString(row.getCell(7)));
            transaction.setSubBrand(getCellValueAsString(row.getCell(8)));
            transaction.setCountry(getCellValueAsString(row.getCell(9)));
            transaction.setCity(getCellValueAsString(row.getCell(10)));
            transaction.setArea(getCellValueAsString(row.getCell(11)));
            transaction.setRetailerGroup(getCellValueAsString(row.getCell(12)));
            transaction.setRetailerSubGroup(getCellValueAsString(row.getCell(13)));
            transaction.setChannel(getCellValueAsString(row.getCell(14)));
            transaction.setSubChannel(getCellValueAsString(row.getCell(15)));
            transaction.setSalesmen(getCellValueAsString(row.getCell(16)));
            transaction.setOrderNumber(getCellValueAsString(row.getCell(17)));
            transaction.setCustomer(getCellValueAsString(row.getCell(18)));
            transaction.setCustomerAccountName(getCellValueAsString(row.getCell(19)));
            transaction.setCustomerAccountNumber(getCellValueAsString(row.getCell(20)));
            transaction.setItem(getCellValueAsString(row.getCell(21)));
            transaction.setItemDescription(getCellValueAsString(row.getCell(22)));
            transaction.setPromoItem(getCellValueAsString(row.getCell(23)));
            transaction.setFocNonFoc(getCellValueAsString(row.getCell(24)));
            transaction.setUnitSellingPrice(getCellValueAsBigDecimal(row.getCell(25)));
            transaction.setInvoiceNumber(getCellValueAsString(row.getCell(26)));
            transaction.setInvoiceDate(getCellValueAsDate(row.getCell(27)));
            transaction.setYear(getCellValueAsInteger(row.getCell(28)));
            transaction.setMonth(getCellValueAsString(row.getCell(29)));
            transaction.setInvoicedQuantity(getCellValueAsInteger(row.getCell(30)));
            transaction.setValue(getCellValueAsBigDecimal(row.getCell(31)));

            return transaction;
        } catch (Exception e) {
            log.error("Error parsing row: {}", e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
            }
        } catch (Exception e) {
            log.warn("Error converting cell to BigDecimal: {}", e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Integer.parseInt(value);
            }
        } catch (Exception e) {
            log.warn("Error converting cell to Integer: {}", e.getMessage());
        }
        return null;
    }

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception e) {
            log.warn("Error converting cell to Date: {}", e.getMessage());
        }
        return null;
    }
}
