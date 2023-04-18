package ru.explore.with.me.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class AdviceHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationException(ValidationException exc) {
        log.error(exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFound(NotFoundException exc) {
        log.error(exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
    }
}
