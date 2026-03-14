package com.cloudanalytics.controller;

import com.cloudanalytics.dto.EventRequest;
import com.cloudanalytics.dto.EventResponse;
import com.cloudanalytics.dto.PageResponse;
import com.cloudanalytics.entity.User;
import com.cloudanalytics.service.EventService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Events", description = "Event ingestion and query endpoints")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Ingest a single event")
    public EventResponse ingest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody EventRequest request) {
        return eventService.ingest(user.getTenantId(), request);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Ingest a batch of events (up to 1000)")
    public List<EventResponse> ingestBatch(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody List<EventRequest> requests) {
        if (requests.size() > 1000) {
            throw new IllegalArgumentException("Batch size cannot exceed 1000 events");
        }
        return eventService.ingestBatch(user.getTenantId(), requests);
    }

    @GetMapping
    @Operation(summary = "Query events with optional filters")
    public PageResponse<EventResponse> listEvents(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return eventService.listEvents(user.getTenantId(), page, Math.min(size, 100),
                source, eventType, from, to);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public EventResponse getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return eventService.getById(user.getTenantId(), id);
    }

    @GetMapping("/sources")
    @Operation(summary = "List distinct event sources for the current tenant")
    public List<String> getSources(@AuthenticationPrincipal User user) {
        return eventService.getSources(user.getTenantId());
    }
}
