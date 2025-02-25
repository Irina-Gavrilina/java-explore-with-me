package ru.practicum.ewm.exception;

public class DuplicatedException extends RuntimeException {

    public DuplicatedException(final String message) {
        super(message);
    }
}