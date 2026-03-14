package com.cloudanalytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "metrics", indexes = {
    @Index(name = "idx_metrics_name_time", columnList = "metric_name, recorded_at"),
    @Index(name = "idx_metrics_tenant", columnList = "tenant_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "metric_name", nullable = false, length = 200)
    private String metricName;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal value;

    @Column(length = 50)
    private String unit;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(length = 50)
    private String aggregationType; // SUM, AVG, COUNT, MAX, MIN

    @Column(columnDefinition = "TEXT")
    private String tags; // JSON string of key-value tags
}
