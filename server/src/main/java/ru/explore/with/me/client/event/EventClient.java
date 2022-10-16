package ru.explore.with.me.client.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.explore.with.me.client.BaseClient;
import ru.explore.with.me.client.mapper.EventHitMapper;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

    public void sendStatistic(HttpServletRequest request) {
        post("/hit", mapper.toEndpointHitDto(request));
    }

    /**
     * Получение статисти
     *
     * @param eventId
     * @param start
     * @return
     */
    public EventViewStats getEventStatistic(Long eventId, LocalDateTime start, LocalDateTime end) {
        String startCoded = URLEncoder.encode(
                start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8);
        String endCoded = URLEncoder.encode
                (end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), StandardCharsets.UTF_8);
//        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("msisdn", "{msisdn}")
//                .queryParam("email", "{email}")
//                .queryParam("clientVersion", "{clientVersion}")
//                .queryParam("clientType", "{clientType}")
//                .queryParam("issuerName", "{issuerName}")
//                .queryParam("applicationName", "{applicationName}")
//                .encode()
//                .toUriString();

        Map<String, Object> parameters = Map.of(
                "start", startCoded,
                "end", endCoded,
                "uris", "/events/" + eventId,
                "unique", true);
        return mapper.toEventViewStats(
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters));
    }
}
