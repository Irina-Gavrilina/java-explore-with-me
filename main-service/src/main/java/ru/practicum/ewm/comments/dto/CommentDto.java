package ru.practicum.ewm.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.CommentStatus;
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
public class CommentDto {

    Long id;
    Long eventId;
    UserShortDto author;
    String text;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime createdOn;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime lastUpdatedOn;
    CommentStatus status;
}