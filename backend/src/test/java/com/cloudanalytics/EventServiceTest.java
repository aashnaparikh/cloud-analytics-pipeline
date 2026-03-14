package com.cloudanalytics;

import com.cloudanalytics.dto.EventRequest;
import com.cloudanalytics.entity.Event;
import com.cloudanalytics.repository.EventRepository;
import com.cloudanalytics.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private EventRequest request;
    private Event savedEvent;

    @BeforeEach
    void setUp() {
        request = new EventRequest();
        request.setSource("web-app");
        request.setEventType("page_view");
        request.setNumericValue(BigDecimal.valueOf(42.0));
        request.setEventTimestamp(Instant.now());

        savedEvent = Event.builder()
                .id(UUID.randomUUID())
                .source("web-app")
                .eventType("page_view")
                .tenantId("demo")
                .numericValue(BigDecimal.valueOf(42.0))
                .eventTimestamp(request.getEventTimestamp())
                .ingestedAt(Instant.now())
                .status(Event.EventStatus.PROCESSED)
                .build();
    }

    @Test
    void ingest_shouldSaveEventAndReturnResponse() {
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        var response = eventService.ingest("demo", request);

        assertThat(response.getId()).isEqualTo(savedEvent.getId());
        assertThat(response.getSource()).isEqualTo("web-app");
        assertThat(response.getEventType()).isEqualTo("page_view");
        assertThat(response.getTenantId()).isEqualTo("demo");
        assertThat(response.getStatus()).isEqualTo(Event.EventStatus.PROCESSED);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void ingest_shouldSetTenantIdFromParameter() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            assertThat(e.getTenantId()).isEqualTo("acme-corp");
            return savedEvent;
        });

        eventService.ingest("acme-corp", request);

        verify(eventRepository).save(any(Event.class));
    }
}
