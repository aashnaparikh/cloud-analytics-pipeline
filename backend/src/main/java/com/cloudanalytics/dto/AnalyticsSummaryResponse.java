package com.cloudanalytics.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {
    private long totalEvents;
    private long eventsLast24h;
    private long eventsLastHour;
    private Map<String, Long> eventsBySource;
    private Map<String, Long> eventsByType;
    private List<TimeSeriesPoint> eventTimeSeries;
    private List<MetricSummary> metricSummaries;
    private Instant generatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesPoint {
        private Instant timestamp;
        private long count;
        private BigDecimal value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricSummary {
        private String metricName;
        private BigDecimal avg;
        private BigDecimal min;
        private BigDecimal max;
        private BigDecimal sum;
        private long count;
        private String unit;
    }
}
