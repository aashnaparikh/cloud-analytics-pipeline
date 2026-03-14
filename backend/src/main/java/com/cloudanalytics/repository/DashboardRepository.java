package com.cloudanalytics.repository;

import com.cloudanalytics.entity.Dashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    Page<Dashboard> findByTenantIdOrderByUpdatedAtDesc(String tenantId, Pageable pageable);
    Optional<Dashboard> findByIdAndTenantId(UUID id, String tenantId);
    Page<Dashboard> findByTenantIdAndCreatedByOrderByUpdatedAtDesc(
            String tenantId, String createdBy, Pageable pageable);
}
