package ru.explore.with.me.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class EndpointHitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
