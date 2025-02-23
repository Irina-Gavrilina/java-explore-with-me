package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.user.dto.UserShortDto;
import java.time.LocalDateTime;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests = 0L;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit = 0;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime publishedOn;
    Boolean requestModeration = true;
    EventState state;
    String title;
    Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventFullDto)) return false;
        return id != null && id.equals(((EventFullDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}