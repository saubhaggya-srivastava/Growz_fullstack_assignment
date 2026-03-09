package com.growz.analytics.repository;

import com.growz.analytics.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Region entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    // Spring Data JPA will provide basic CRUD operations
}
