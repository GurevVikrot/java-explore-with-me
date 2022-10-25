package ru.explore.with.me.statistic.util;

import java.time.format.DateTimeFormatter;

/**
 * Класс паттерна преобразования LocalDateTime
 */
public class CustomTimeFormatter {
    public static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
