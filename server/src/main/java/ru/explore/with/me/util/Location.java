package ru.explore.with.me.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Класс геопозиции. Имеет два поля:
 * double lat - Широта
 * double lon - Долгота
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class Location {
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private float lat;
    @DecimalMin(value = "-179.999999")
    @DecimalMax(value = "180.0")
    private float lon;
}
