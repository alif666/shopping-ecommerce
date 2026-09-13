package com.example.demo.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.NoSuchElementException;

/** Handles request errors consistently for every REST controller. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> handleBadRequest(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> handleInvalidParameter(MethodArgumentTypeMismatchException exception) {
        String message = "Parameter '%s' must be a valid %s".formatted(
                exception.getName(), exception.getRequiredType().getSimpleName());
        return Map.of("message", message);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> handleNotFound(NoSuchElementException exception) {
        return Map.of("message", exception.getMessage());
    }
}
