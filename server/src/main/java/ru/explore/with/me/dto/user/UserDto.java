package ru.explore.with.me.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Dto класс пользователя. Используется для создания нового пользователя и ответа
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@NotNull
public class UserDto {
    private long id;
    @Email
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
