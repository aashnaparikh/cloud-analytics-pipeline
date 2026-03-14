package com.cloudanalytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class MetricRequest {
    @NotBlank(message = "Metric name is required")
    private String metricName;

    @NotNull(message = "Value is required")
    private BigDecimal value;

    private String unit;

    @NotNull(message = "Recorded at is required")
    private Instant recordedAt;

    private String aggregationType;
    private String tags;
}
