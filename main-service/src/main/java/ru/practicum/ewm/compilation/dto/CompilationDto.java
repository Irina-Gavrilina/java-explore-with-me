package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.EventShortDto;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    Long id;
    Set<EventShortDto> events;
    Boolean pinned;
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompilationDto)) return false;
        return id != null && id.equals(((CompilationDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}