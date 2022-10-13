package ru.explore.with.me.util;

/**
 * ENUM класс статустов событий (Event)
 *     WAITING - Ожидание публикации
 *     CANCELED - Отменено
 *     PUBLISHED - Опубликовано
 *     ENDED - Завершено
 */
public enum EventStatus {
    WAITING,
    REJECTED,
    PUBLISHED,
    ENDED
}
