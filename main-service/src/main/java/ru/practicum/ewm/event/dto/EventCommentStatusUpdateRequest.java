package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.CommentStatus;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCommentStatusUpdateRequest {

    @NotNull
    Set<Long> commentIds;
    @NotNull
    CommentStatus status;
}