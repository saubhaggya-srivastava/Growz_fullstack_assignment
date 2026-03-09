package com.growz.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application class for Sales & Store Analytics System.
 * 
 * This application provides REST APIs for analyzing FMCG sales data and active store metrics.
 * Features include:
 * - Sales analytics with YoY comparisons
 * - Active store tracking and metrics
 * - Data ingestion from CSV/Excel files
 * - Caching for improved performance
 * - Swagger/OpenAPI documentation
 * 
 * @author Growz Analytics Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
public class AnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }
}
