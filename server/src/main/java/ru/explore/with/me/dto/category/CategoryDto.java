package ru.explore.with.me.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NotNull
public class CategoryDto {
    private int id;
    @NotBlank
    @NotEmpty
    private String name;
}
