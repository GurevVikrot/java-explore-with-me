package ru.explore.with.me.exeption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String massage) {
        super(massage);
    }
}
