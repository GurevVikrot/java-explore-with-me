package ru.explore.with.me.util;

/**
 * Enum Класс статуса заявки на участие в событии
 *     PENDING - Ожидает согласования
 *     CONFIRMED - Подтверждена
 *     CANCELED - Завершена по какой-либо причине
 *     REJECTED - Отклонена владельцем события
 */
public enum ParticipantStatus {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED
}
