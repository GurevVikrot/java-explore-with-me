package ru.explore.with.me.statistic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.explore.with.me.statistic.util.CustomTimeFormatter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@ToString
public class EndpointHitDto {
    private long id;
    @NotEmpty
    private String app;
    @NotEmpty
    private String uri;
    @NotEmpty
    private String ip;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @PastOrPresent
    private LocalDateTime timestamp;

}
