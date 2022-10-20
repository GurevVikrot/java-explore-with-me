package ru.explore.with.me.client.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.explore.with.me.client.dto.EndpointHitDto;
import ru.explore.with.me.client.event.model.EventViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Маппер запросов и ответов от сервиса статистики
 */
@Component
@Slf4j
public class EventHitMapper {
    /**
     * Преобразование данных обращения к основному эндпоинту в EndpointHitDto.
     *
     * @param request данные обращения к основному эндпоинту
     * @return EndpointHitDto
     */
    public EndpointHitDto toEndpointHitDto(HttpServletRequest request) {
        return new EndpointHitDto(
                0,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }

    /**
     * Преобразование ответа сервиса статистики в объект EventViewStats.
     * В случае ошибки преобразования отлавливается исключение и логируется,
     * для исключения падения основного сервиса и выдачи ошибки основному клиенту.
     *
     * @param response ResponseEntity ответа сервиса статистики
     * @return EventViewStats
     */
    public EventViewStats toEventViewStats(ResponseEntity<Object> response) {
        if (response != null && response.hasBody()) {
            try {
                List<Map<String, Object>> bodies = (List<Map<String, Object>>) response.getBody();
                List<EventViewStats> stats = new ArrayList<>();

                for (Map<String, Object> map : bodies) {
                    stats.add(new EventViewStats(
                            (String) map.get("app"),
                            (String) map.get("uri"),
                            (Integer) map.get("hits")));
                }

                return stats.get(0);
            } catch (Exception exception) {
                log.error("При десериализации ответа статистики произошла ошибка. Получен неверный JSON {}",
                        response.getBody());
            }
        }

        log.error("При запросе статистики возвращени null. Маппер вернул 0 просмотров");
        return new EventViewStats("", "", 0);
    }
}
