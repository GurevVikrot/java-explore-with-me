package ru.explore.with.me.client.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.explore.with.me.client.dto.EndpointHitDto;
import ru.explore.with.me.client.event.EventViewStats;
import ru.explore.with.me.util.CustomTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
@Slf4j
public class EventHitMapper {
    public EndpointHitDto toEndpointHitDto (HttpServletRequest request) {
        return new EndpointHitDto(
                0,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }

    public EventViewStats toEventViewStats(ResponseEntity<Object> response) {
        if (response!= null && response.hasBody()) {
            try {
                return (EventViewStats) response.getBody();
            } catch (Exception exception) {
                log.error("При десериализации ответа статистики произошла ошибка. Получен неверный JSON {}",
                        response.getBody());
            }
        }

        log.error("При запросе статистики возвращени null. Маппер вернул 0 просмотров");
        return new EventViewStats("", "", 0);
    }
}
