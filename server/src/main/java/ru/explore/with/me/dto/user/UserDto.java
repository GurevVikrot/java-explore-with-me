package ru.explore.with.me.dto.user;

import lombok.*;
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
    private LocalDateTime created;
}
