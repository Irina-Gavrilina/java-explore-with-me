package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 символов")
    String name;
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина e-mail должна быть от 6 до 254 символов")
    @Email(message = "Неверный формат e-mail")
    String email;
}