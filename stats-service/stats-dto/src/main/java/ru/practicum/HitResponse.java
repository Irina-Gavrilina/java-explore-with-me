package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitResponse {

    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HitResponse)) return false;
        return id != null && id.equals(((HitResponse) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}