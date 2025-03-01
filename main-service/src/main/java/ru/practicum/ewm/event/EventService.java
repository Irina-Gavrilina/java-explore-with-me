package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getAllEventsByUserId(long userId, int from, int size);

    EventFullDto createEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdAndOwnerId(long userId, long eventId);

    EventFullDto updateEventByOwner(long userId, long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getAllParticipationRequestsByEventOwnerId(long userId, long eventId);

    EventRequestStatusUpdateResult changeParticipationRequestStatus(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest request);

    List<CommentDto> getAllCommentsByEventOwnerId(long userId, long eventId);

    EventCommentStatusUpdateResult changeCommentStatus(long userId,
                                                       long eventId,
                                                       EventCommentStatusUpdateRequest request);

    List<EventShortDto> getPublicEventsWithParams(String text,
                                                  List<Integer> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request);

    EventFullDto getPublicEventById(long eventId, HttpServletRequest request);

    List<EventFullDto> getAdminEventsWithParams(List<Integer> users,
                                                List<String> states,
                                                List<Integer> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request);
}