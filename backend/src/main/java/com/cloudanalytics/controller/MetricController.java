package com.cloudanalytics.controller;

import com.cloudanalytics.dto.AnalyticsSummaryResponse;
import com.cloudanalytics.dto.MetricRequest;
import com.cloudanalytics.entity.Metric;
import com.cloudanalytics.entity.User;
import com.cloudanalytics.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Metrics", description = "Time-series metric ingestion and query endpoints")
public class MetricController {

    private final MetricService metricService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Record a single metric data point")
    public Metric record(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MetricRequest request) {
        return metricService.record(user.getTenantId(), request);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Record multiple metric data points")
    public List<Metric> recordBatch(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody List<MetricRequest> requests) {
        return metricService.recordBatch(user.getTenantId(), requests);
    }

    @GetMapping("/names")
    @Operation(summary = "List available metric names for this tenant")
    public List<String> getMetricNames(@AuthenticationPrincipal User user) {
        return metricService.getMetricNames(user.getTenantId());
    }

    @GetMapping("/timeseries")
    @Operation(summary = "Get hourly time series for a metric")
    public List<AnalyticsSummaryResponse.TimeSeriesPoint> getTimeSeries(
            @AuthenticationPrincipal User user,
            @RequestParam String metricName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant resolvedFrom = from != null ? from : Instant.now().minus(24, ChronoUnit.HOURS);
        Instant resolvedTo = to != null ? to : Instant.now();
        return metricService.getTimeSeries(user.getTenantId(), metricName, resolvedFrom, resolvedTo);
    }

    @GetMapping("/aggregates")
    @Operation(summary = "Get aggregate stats for all metrics in a time range")
    public List<AnalyticsSummaryResponse.MetricSummary> getAggregates(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant resolvedFrom = from != null ? from : Instant.now().minus(24, ChronoUnit.HOURS);
        Instant resolvedTo = to != null ? to : Instant.now();
        return metricService.getAggregates(user.getTenantId(), resolvedFrom, resolvedTo);
    }
}
