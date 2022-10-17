package ru.explore.with.me.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.explore.with.me.dto.category.CategoryDto;
import ru.explore.with.me.dto.user.UserDto;
import ru.explore.with.me.dto.user.UserShortDto;
import ru.explore.with.me.util.Location;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private boolean paid;
    private int confirmedRequests;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private int views;
    private Location location;
}
