package com.cloudanalytics.repository;

import com.cloudanalytics.entity.Metric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetricRepository extends JpaRepository<Metric, UUID> {

    Page<Metric> findByTenantIdAndMetricNameOrderByRecordedAtDesc(
            String tenantId, String metricName, Pageable pageable);

    @Query("""
        SELECT m.metricName, AVG(m.value), MIN(m.value), MAX(m.value), SUM(m.value), COUNT(m)
        FROM Metric m
        WHERE m.tenantId = :tenantId
          AND m.recordedAt BETWEEN :from AND :to
        GROUP BY m.metricName
        ORDER BY m.metricName
    """)
    List<Object[]> aggregateByMetricNameInRange(
            @Param("tenantId") String tenantId,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
        SELECT DATE_TRUNC('hour', m.recordedAt) as hour, AVG(m.value) as avg_value
        FROM Metric m
        WHERE m.tenantId = :tenantId
          AND m.metricName = :metricName
          AND m.recordedAt BETWEEN :from AND :to
        GROUP BY hour
        ORDER BY hour
    """)
    List<Object[]> getTimeSeriesByHour(
            @Param("tenantId") String tenantId,
            @Param("metricName") String metricName,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
        SELECT DISTINCT m.metricName FROM Metric m
        WHERE m.tenantId = :tenantId
        ORDER BY m.metricName
    """)
    List<String> findDistinctMetricNames(@Param("tenantId") String tenantId);

    @Query("SELECT AVG(m.value) FROM Metric m WHERE m.tenantId = :tenantId AND m.metricName = :metricName AND m.recordedAt BETWEEN :from AND :to")
    BigDecimal getAverageValue(
            @Param("tenantId") String tenantId,
            @Param("metricName") String metricName,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
