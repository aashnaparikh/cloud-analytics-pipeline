package com.cloudanalytics.dto;

import com.cloudanalytics.entity.Event;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class EventResponse {
    private UUID id;
    private String source;
    private String eventType;
    private String tenantId;
    private String payload;
    private BigDecimal numericValue;
    private String stringValue;
    private Instant eventTimestamp;
    private Instant ingestedAt;
    private Event.EventStatus status;
}
