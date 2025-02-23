package ru.practicum.ewm.exception;

public class ValidationConflict extends RuntimeException {

    public ValidationConflict(String message) {
        super(message);
    }
}