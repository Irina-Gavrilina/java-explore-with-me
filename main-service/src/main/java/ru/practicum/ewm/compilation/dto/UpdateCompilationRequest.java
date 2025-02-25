package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {

    Set<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть от 1 до 50 символов")
    String title;

    public boolean hasEvents() {
        return events != null;
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return title != null;
    }
}