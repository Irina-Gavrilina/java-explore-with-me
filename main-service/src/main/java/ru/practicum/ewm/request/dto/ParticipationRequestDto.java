package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.RequestStatus;
import java.time.LocalDateTime;
import static ru.practicum.client.constants.Constants.DATE_FORMAT;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    Long id;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime created;
    Long event;
    Long requester;
    RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipationRequestDto)) return false;
        return id != null && id.equals(((ParticipationRequestDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}