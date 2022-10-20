package ru.explore.with.me.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Dto класс с краткой информацией о пользователе. Используется для ответа
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class UserShortDto {
    private long id;
    private String name;
}
