package com.cloudanalytics.repository;

import com.cloudanalytics.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByTenantIdOrderByEventTimestampDesc(String tenantId, Pageable pageable);

    Page<Event> findByTenantIdAndSourceOrderByEventTimestampDesc(
            String tenantId, String source, Pageable pageable);

    Page<Event> findByTenantIdAndEventTypeOrderByEventTimestampDesc(
            String tenantId, String eventType, Pageable pageable);

    @Query("""
        SELECT e FROM Event e
        WHERE e.tenantId = :tenantId
          AND e.eventTimestamp BETWEEN :from AND :to
        ORDER BY e.eventTimestamp DESC
    """)
    Page<Event> findByTenantIdAndTimeRange(
            @Param("tenantId") String tenantId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);

    @Query("""
        SELECT e.source, e.eventType, COUNT(e) as count
        FROM Event e
        WHERE e.tenantId = :tenantId
          AND e.eventTimestamp BETWEEN :from AND :to
        GROUP BY e.source, e.eventType
        ORDER BY count DESC
    """)
    List<Object[]> countBySourceAndTypeInRange(
            @Param("tenantId") String tenantId,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
        SELECT DATE_TRUNC('hour', e.eventTimestamp) as hour, COUNT(e) as count
        FROM Event e
        WHERE e.tenantId = :tenantId
          AND e.eventTimestamp BETWEEN :from AND :to
        GROUP BY hour
        ORDER BY hour
    """)
    List<Object[]> countByHourInRange(
            @Param("tenantId") String tenantId,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
        SELECT DISTINCT e.source FROM Event e
        WHERE e.tenantId = :tenantId
    """)
    List<String> findDistinctSourcesByTenantId(@Param("tenantId") String tenantId);

    long countByTenantIdAndEventTimestampBetween(String tenantId, Instant from, Instant to);
}
