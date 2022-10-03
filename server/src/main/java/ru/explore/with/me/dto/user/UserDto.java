package ru.explore.with.me.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Table;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String name;
    private LocalDateTime created;
    private LocalDate birthday;
}
