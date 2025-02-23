package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {

    final List<String> errors;
    final String message;
    final String reason;
    final String status;
    @JsonFormat(pattern = DATE_FORMAT)
    final LocalDateTime timestamp;
}