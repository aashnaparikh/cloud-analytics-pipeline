package com.cloudanalytics.service;

import com.cloudanalytics.dto.EventRequest;
import com.cloudanalytics.dto.EventResponse;
import com.cloudanalytics.dto.PageResponse;
import com.cloudanalytics.entity.Event;
import com.cloudanalytics.exception.ResourceNotFoundException;
import com.cloudanalytics.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse ingest(String tenantId, EventRequest request) {
        Event event = Event.builder()
                .source(request.getSource())
                .eventType(request.getEventType())
                .tenantId(tenantId)
                .payload(request.getPayload())
                .numericValue(request.getNumericValue())
                .stringValue(request.getStringValue())
                .eventTimestamp(request.getEventTimestamp())
                .status(Event.EventStatus.PROCESSED)
                .build();

        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public List<EventResponse> ingestBatch(String tenantId, List<EventRequest> requests) {
        List<Event> events = requests.stream()
                .map(req -> Event.builder()
                        .source(req.getSource())
                        .eventType(req.getEventType())
                        .tenantId(tenantId)
                        .payload(req.getPayload())
                        .numericValue(req.getNumericValue())
                        .stringValue(req.getStringValue())
                        .eventTimestamp(req.getEventTimestamp())
                        .status(Event.EventStatus.PROCESSED)
                        .build())
                .toList();

        return eventRepository.saveAll(events).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> listEvents(String tenantId, int page, int size,
                                                   String source, String eventType,
                                                   Instant from, Instant to) {
        var pageable = PageRequest.of(page, size, Sort.by("eventTimestamp").descending());

        if (from != null && to != null) {
            return PageResponse.from(
                    eventRepository.findByTenantIdAndTimeRange(tenantId, from, to, pageable)
                            .map(this::toResponse));
        }
        if (source != null) {
            return PageResponse.from(
                    eventRepository.findByTenantIdAndSourceOrderByEventTimestampDesc(tenantId, source, pageable)
                            .map(this::toResponse));
        }
        if (eventType != null) {
            return PageResponse.from(
                    eventRepository.findByTenantIdAndEventTypeOrderByEventTimestampDesc(tenantId, eventType, pageable)
                            .map(this::toResponse));
        }
        return PageResponse.from(
                eventRepository.findByTenantIdOrderByEventTimestampDesc(tenantId, pageable)
                        .map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public EventResponse getById(String tenantId, UUID id) {
        return eventRepository.findById(id)
                .filter(e -> e.getTenantId().equals(tenantId))
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<String> getSources(String tenantId) {
        return eventRepository.findDistinctSourcesByTenantId(tenantId);
    }

    private EventResponse toResponse(Event event) {
        var resp = new EventResponse();
        resp.setId(event.getId());
        resp.setSource(event.getSource());
        resp.setEventType(event.getEventType());
        resp.setTenantId(event.getTenantId());
        resp.setPayload(event.getPayload());
        resp.setNumericValue(event.getNumericValue());
        resp.setStringValue(event.getStringValue());
        resp.setEventTimestamp(event.getEventTimestamp());
        resp.setIngestedAt(event.getIngestedAt());
        resp.setStatus(event.getStatus());
        return resp;
    }
}
