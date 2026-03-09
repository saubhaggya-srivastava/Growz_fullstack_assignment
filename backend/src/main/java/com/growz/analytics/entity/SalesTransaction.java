package com.growz.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Complete Sales Transaction entity with all 32 columns from Excel.
 * This represents the full sales data structure.
 */
@Entity
@Table(
    name = "sales_transactions",
    indexes = {
        @Index(name = "idx_invoice_date", columnList = "invoiceDate"),
        @Index(name = "idx_year_month", columnList = "\"year\",\"month\""),
        @Index(name = "idx_brand", columnList = "brand"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_customer", columnList = "customer"),
        @Index(name = "idx_channel", columnList = "channel")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Distributor Information
    @Column(name = "master_distributor", length = 200)
    private String masterDistributor;

    @Column(name = "distributor", length = 200)
    private String distributor;

    @Column(name = "line_of_business", length = 200)
    private String lineOfBusiness;

    @Column(name = "supplier", length = 300)
    private String supplier;

    // Product Information
    @Column(name = "agency", length = 200)
    private String agency;

    @Column(name = "category", length = 200)
    private String category;

    @Column(name = "segment", length = 200)
    private String segment;

    @Column(name = "brand", length = 200)
    private String brand;

    @Column(name = "sub_brand", length = 200)
    private String subBrand;

    // Location Information
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 200)
    private String city;

    @Column(name = "area", length = 200)
    private String area;

    // Retailer Information
    @Column(name = "retailer_group", length = 200)
    private String retailerGroup;

    @Column(name = "retailer_sub_group", length = 200)
    private String retailerSubGroup;

    // Channel Information
    @Column(name = "channel", length = 200)
    private String channel;

    @Column(name = "sub_channel", length = 200)
    private String subChannel;

    // Sales Information
    @Column(name = "salesmen", length = 200)
    private String salesmen;

    @Column(name = "order_number", length = 100)
    private String orderNumber;

    // Customer Information
    @Column(name = "customer", length = 300)
    private String customer;

    @Column(name = "customer_account_name", length = 300)
    private String customerAccountName;

    @Column(name = "customer_account_number", length = 100)
    private String customerAccountNumber;

    // Item Information
    @Column(name = "item", length = 200)
    private String item;

    @Column(name = "item_description", length = 500)
    private String itemDescription;

    @Column(name = "promo_item", length = 100)
    private String promoItem;

    @Column(name = "foc_non_foc", length = 50)
    private String focNonFoc;

    @Column(name = "unit_selling_price", precision = 15, scale = 2)
    private BigDecimal unitSellingPrice;

    // Invoice Information
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "\"year\"")
    private Integer year;

    @Column(name = "\"month\"", length = 20)
    private String month;

    // Transaction Values
    @Column(name = "invoiced_quantity")
    private Integer invoicedQuantity;

    @Column(name = "\"value\"", precision = 15, scale = 2)
    private BigDecimal value;
}
