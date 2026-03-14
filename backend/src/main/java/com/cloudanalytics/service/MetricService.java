package com.cloudanalytics.service;

import com.cloudanalytics.dto.AnalyticsSummaryResponse;
import com.cloudanalytics.dto.MetricRequest;
import com.cloudanalytics.entity.Metric;
import com.cloudanalytics.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;

    @Transactional
    @CacheEvict(value = {"metrics", "analytics-summary"}, allEntries = true)
    public Metric record(String tenantId, MetricRequest request) {
        return metricRepository.save(Metric.builder()
                .metricName(request.getMetricName())
                .tenantId(tenantId)
                .value(request.getValue())
                .unit(request.getUnit())
                .recordedAt(request.getRecordedAt())
                .aggregationType(request.getAggregationType())
                .tags(request.getTags())
                .build());
    }

    @Transactional
    @CacheEvict(value = {"metrics", "analytics-summary"}, allEntries = true)
    public List<Metric> recordBatch(String tenantId, List<MetricRequest> requests) {
        List<Metric> metrics = requests.stream()
                .map(req -> Metric.builder()
                        .metricName(req.getMetricName())
                        .tenantId(tenantId)
                        .value(req.getValue())
                        .unit(req.getUnit())
                        .recordedAt(req.getRecordedAt())
                        .aggregationType(req.getAggregationType())
                        .tags(req.getTags())
                        .build())
                .toList();
        return metricRepository.saveAll(metrics);
    }

    @Cacheable(value = "metrics", key = "#tenantId + ':names'")
    @Transactional(readOnly = true)
    public List<String> getMetricNames(String tenantId) {
        return metricRepository.findDistinctMetricNames(tenantId);
    }

    @Cacheable(value = "metrics", key = "#tenantId + ':' + #metricName + ':' + #from + ':' + #to")
    @Transactional(readOnly = true)
    public List<AnalyticsSummaryResponse.TimeSeriesPoint> getTimeSeries(
            String tenantId, String metricName, Instant from, Instant to) {
        return metricRepository.getTimeSeriesByHour(tenantId, metricName, from, to).stream()
                .map(row -> AnalyticsSummaryResponse.TimeSeriesPoint.builder()
                        .timestamp((Instant) row[0])
                        .value(BigDecimal.valueOf(((Number) row[1]).doubleValue()))
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable(value = "metrics", key = "#tenantId + ':aggregates:' + #from + ':' + #to")
    @Transactional(readOnly = true)
    public List<AnalyticsSummaryResponse.MetricSummary> getAggregates(
            String tenantId, Instant from, Instant to) {
        return metricRepository.aggregateByMetricNameInRange(tenantId, from, to).stream()
                .map(row -> AnalyticsSummaryResponse.MetricSummary.builder()
                        .metricName((String) row[0])
                        .avg(BigDecimal.valueOf(((Number) row[1]).doubleValue()))
                        .min(BigDecimal.valueOf(((Number) row[2]).doubleValue()))
                        .max(BigDecimal.valueOf(((Number) row[3]).doubleValue()))
                        .sum(BigDecimal.valueOf(((Number) row[4]).doubleValue()))
                        .count(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}
