package ru.explore.with.me.client.mapper;

import org.springframework.stereotype.Component;
import ru.explore.with.me.client.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class EndpointHitMapper {
    public EndpointHitDto toEndpointHitDto (HttpServletRequest request) {
        return new EndpointHitDto(
                0,
                "exploreWithMe",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }
}
