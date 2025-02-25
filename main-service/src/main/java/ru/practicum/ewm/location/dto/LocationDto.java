package ru.practicum.ewm.location.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {

    @NotNull
    @Min(value = -90, message = "Широта должна быть не меньше -90 градусов")
    @Max(value = 90, message = "Широта должна быть не больше 90 градусов")
    @Column(name = "lat", nullable = false)
    Float lat;

    @NotNull
    @Min(value = -180, message = "Долгота должна быть не меньше -180 градусов")
    @Max(value = 180, message = "Долгота должна быть не больше 180 градусов")
    @Column(name = "lon", nullable = false)
    Float lon;
}