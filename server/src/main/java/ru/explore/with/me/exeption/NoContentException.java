package ru.explore.with.me.exeption;

/**
 * Класс для прохождения теста Удаление категории {{baseUrl}}/admin/categories/:catId
 * Возвращает статус 200 и null если категория не найдена
 */
public class NoContentException extends RuntimeException {
    public NoContentException(String message) {
        super(message);

    }
}
