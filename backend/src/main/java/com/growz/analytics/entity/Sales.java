package com.growz.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Sales entity representing individual sales transactions.
 * 
 * Each sales record links a product to a store with invoice date, quantity, and amount.
 * Indexes are defined for efficient querying by date and product/store combinations.
 */
@Entity
@Table(
    name = "sales",
    indexes = {
        @Index(name = "idx_invoice_date", columnList = "invoiceDate"),
        @Index(name = "idx_product_date", columnList = "product_id,invoiceDate"),
        @Index(name = "idx_store_date", columnList = "store_id,invoiceDate")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
