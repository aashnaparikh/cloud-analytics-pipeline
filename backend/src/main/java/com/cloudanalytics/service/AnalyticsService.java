package com.cloudanalytics.service;

import com.cloudanalytics.dto.AnalyticsSummaryResponse;
import com.cloudanalytics.repository.EventRepository;
import com.cloudanalytics.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final EventRepository eventRepository;
    private final MetricRepository metricRepository;
    private final MetricService metricService;

    @Cacheable(value = "analytics-summary", key = "#tenantId")
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary(String tenantId) {
        Instant now = Instant.now();
        Instant last24h = now.minus(24, ChronoUnit.HOURS);
        Instant lastHour = now.minus(1, ChronoUnit.HOURS);
        Instant last7Days = now.minus(7, ChronoUnit.DAYS);

        long totalEvents = eventRepository.count();
        long eventsLast24h = eventRepository.countByTenantIdAndEventTimestampBetween(tenantId, last24h, now);
        long eventsLastHour = eventRepository.countByTenantIdAndEventTimestampBetween(tenantId, lastHour, now);

        // Events by source and type
        List<Object[]> sourceTypeCounts = eventRepository.countBySourceAndTypeInRange(tenantId, last7Days, now);
        Map<String, Long> bySource = new LinkedHashMap<>();
        Map<String, Long> byType = new LinkedHashMap<>();

        for (Object[] row : sourceTypeCounts) {
            String source = (String) row[0];
            String type = (String) row[1];
            long count = ((Number) row[2]).longValue();
            bySource.merge(source, count, Long::sum);
            byType.merge(type, count, Long::sum);
        }

        // Time series — hourly event counts
        List<AnalyticsSummaryResponse.TimeSeriesPoint> timeSeries = eventRepository
                .countByHourInRange(tenantId, last24h, now).stream()
                .map(row -> AnalyticsSummaryResponse.TimeSeriesPoint.builder()
                        .timestamp((Instant) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());

        // Metric aggregates
        List<AnalyticsSummaryResponse.MetricSummary> metricSummaries =
                metricService.getAggregates(tenantId, last24h, now);

        return AnalyticsSummaryResponse.builder()
                .totalEvents(totalEvents)
                .eventsLast24h(eventsLast24h)
                .eventsLastHour(eventsLastHour)
                .eventsBySource(bySource)
                .eventsByType(byType)
                .eventTimeSeries(timeSeries)
                .metricSummaries(metricSummaries)
                .generatedAt(now)
                .build();
    }
}
