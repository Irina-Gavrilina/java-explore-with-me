package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateHitRequest {

    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotBlank
    @Pattern(regexp = "(\\d{1,3}\\.){3}\\d{1,3}", message = "Некорректный IP-адрес")
    String ip;
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime timestamp;
}