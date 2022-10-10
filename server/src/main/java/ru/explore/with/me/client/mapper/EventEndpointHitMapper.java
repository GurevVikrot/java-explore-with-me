package ru.explore.with.me.client.mapper;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.explore.with.me.client.dto.EndpointHitDto;
import ru.explore.with.me.client.event.EventViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class EventEndpointHitMapper {
    public EndpointHitDto toEndpointHitDto (HttpServletRequest request) {
        return new EndpointHitDto(
                0,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }

    public EventViewStats toEventViewStats(ResponseEntity<Object> response) {
        if (response.hasBody()) {
           EventViewStats stats = response.getBody();
        }
    }
}
