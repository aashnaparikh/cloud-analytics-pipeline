package com.cloudanalytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dashboards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(columnDefinition = "TEXT")
    private String configuration; // JSON layout configuration

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
