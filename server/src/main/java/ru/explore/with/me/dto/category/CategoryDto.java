package ru.explore.with.me.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@NotNull
public class CategoryDto {
    private int id;
    @NotBlank
    @NotEmpty
    private String name;
}
