package ru.practicum.ewm.location;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.location.dto.LocationDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}