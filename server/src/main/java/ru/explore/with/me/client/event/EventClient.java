package ru.explore.with.me.client.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.explore.with.me.client.BaseClient;
import ru.explore.with.me.client.event.model.EventViewStats;
import ru.explore.with.me.client.mapper.EventHitMapper;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Реализация клиента RestTemplate для отправки и получения статистики по эндоинтам событий.
 * Обращается к сервису статистики.
 */
@Component
public class EventClient extends BaseClient {

    private final EventHitMapper mapper;

    @Autowired
    public EventClient(@Value("${statistic.url}") String serverUrl, RestTemplateBuilder builder,
                       EventHitMapper mapper) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.mapper = mapper;
    }

    /**
     * Отправка статистики обращения к эндоинту в сервис статистики
     *
     * @param request Данные запроса пришедшие в эндпоинт основного сервиса
     */
    public void sendStatistic(HttpServletRequest request) {
        post("/hit", mapper.toEndpointHitDto(request));
    }

    /**
     * Получение статистики просмотров для конкретного события
     * Запрашивает статистику за промежуток времени.
     *
     * @param eventId id события
     * @param start   Дата и время начала отчета промежутка
     * @return EventViewStats
     */
    public EventViewStats getEventStatistic(Long eventId, LocalDateTime start, LocalDateTime end) {
        String startCoded = URLEncoder.encode(
                start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8);
        String endCoded = URLEncoder.encode
                (end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of(
                "start", startCoded,
                "end", endCoded,
                "uris", "/events/" + eventId,
                "unique", true);
        return mapper.toEventViewStats(
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters));
    }
}
