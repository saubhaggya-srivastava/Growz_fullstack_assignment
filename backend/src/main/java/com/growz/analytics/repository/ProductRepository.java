package com.growz.analytics.repository;

import com.growz.analytics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Data JPA will provide basic CRUD operations
    // Custom queries can be added here if needed
}
