package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.location.Location;
import java.time.LocalDateTime;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000, message = "Длина краткого описания события должна быть от 20 до 2000 символов")
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Длина полного описания события должна быть от 20 до 7000 символов")
    String description;
    @NotNull
    @Future
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime eventDate;
    @NotNull
    @Valid
    Location location;
    Boolean paid = false;
    @PositiveOrZero
    Integer participantLimit = 0;
    boolean requestModeration = true;
    @NotNull
    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 1 до 120 символов")
    String title;
}