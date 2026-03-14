package com.cloudanalytics.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_timestamp", columnList = "event_timestamp"),
    @Index(name = "idx_events_source_type", columnList = "source, event_type"),
    @Index(name = "idx_events_tenant", columnList = "tenant_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String source;

    @NotBlank
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "numeric_value", precision = 20, scale = 6)
    private BigDecimal numericValue;

    @Column(name = "string_value", length = 500)
    private String stringValue;

    @NotNull
    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @CreationTimestamp
    @Column(name = "ingested_at", updatable = false)
    private Instant ingestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.PROCESSED;

    public enum EventStatus {
        PENDING, PROCESSED, FAILED
    }
}
