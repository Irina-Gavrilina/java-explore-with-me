package ru.practicum.ewm.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 7000, message = "Длина комментария должна быть от 1 до 7000 символов")
    private String text;
}