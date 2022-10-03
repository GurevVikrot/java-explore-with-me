package ru.explore.with.me.statistic.dto;

import java.time.LocalDateTime;

public class EndpointHitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
