package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitResponse {

    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;
}