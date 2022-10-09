package ru.explore.with.me.client.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.explore.with.me.client.BaseClient;
import ru.explore.with.me.client.mapper.EndpointHitMapper;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EventClient extends BaseClient {

    private final EndpointHitMapper mapper;

    @Autowired
    public EventClient(@Value("${statistic.url}") String serverUrl, RestTemplateBuilder builder,
                       EndpointHitMapper mapper) {
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

    public ResponseEntity<Object> getEventStatistic(Long eventId, LocalDateTime start) {
        String startCoded = URLEncoder.encode(start.toString(), StandardCharsets.UTF_8);
        String endCoded = URLEncoder.encode(LocalDateTime.now().toString(), StandardCharsets.UTF_8);
        Map<String, Object> parameters = Map.of(
                "start", startCoded,
                "end", endCoded,
                "uris", "",
                "unique", true
                );
        return get("/stats", null, parameters);
    }
}
