package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
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
public class EventShortDto {

    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime eventDate;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventShortDto)) return false;
        return id != null && id.equals(((EventShortDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}