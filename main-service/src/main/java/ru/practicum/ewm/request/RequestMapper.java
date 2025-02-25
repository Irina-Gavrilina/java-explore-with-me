package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toListOfParticipationRequestDto(List<Request> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }
}