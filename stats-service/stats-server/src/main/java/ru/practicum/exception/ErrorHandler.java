package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.security.InvalidParameterException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class,
            MethodArgumentNotValidException.class,
            InvalidParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestFailedException(Exception e) {
        log.error("400, {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.error("500, {}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }
}