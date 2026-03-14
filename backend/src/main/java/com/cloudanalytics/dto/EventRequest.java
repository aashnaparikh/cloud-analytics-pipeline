package com.cloudanalytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EventRequest {
    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Event type is required")
    private String eventType;

    private String payload;
    private BigDecimal numericValue;
    private String stringValue;

    @NotNull(message = "Event timestamp is required")
    private Instant eventTimestamp;
}
