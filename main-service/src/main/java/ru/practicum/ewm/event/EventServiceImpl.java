package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.CreateHitRequest;
import ru.practicum.client.StatsClient;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.comments.Comment;
import ru.practicum.ewm.comments.CommentMapper;
import ru.practicum.ewm.comments.CommentRepository;
import ru.practicum.ewm.comments.CommentStatus;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflict;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final StatsClient statsClient;

    // -------- PRIVATE --------

    @Override
    public List<EventShortDto> getAllEventsByUserId(long userId, int from, int size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorIdWithCategory(userId, pageable);
        return EventMapper.toListOfEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        User user = findUserById(userId);
        Category category = findCategoryById(newEventDto.getCategory());
        Duration duration = Duration.between(LocalDateTime.now(), newEventDto.getEventDate());

        if (duration.toHours() < 2) {
            throw new ValidationException("Дата и время события должны быть не раньше, " +
                    "чем через два часа от текущего момента");
        }

        Event event = EventMapper.toEvent(newEventDto, category, user);
        event.setCreatedOn(LocalDateTime.now());

        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
        event.setLocation(location);

        event.setEventState(EventState.PENDING);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByIdAndOwnerId(long userId, long eventId) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorIdWithCategoryAndLocation(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByOwner(long userId, long eventId, UpdateEventUserRequest request) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorIdWithCategoryAndLocation(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (event.getEventState().equals(EventState.PENDING)
                || event.getEventState().equals(EventState.REJECTED)
                || event.getEventState().equals(EventState.CANCELED)) {

            if (request.hasEventDate()) {
                Duration duration = Duration.between(LocalDateTime.now(), request.getEventDate());

                if (duration.toHours() < 2) {
                    throw new ValidationException("Дата и время события должны быть не раньше, чем через два часа " +
                            "от текущего момента");
                }
                event.setEventDate(request.getEventDate());
            }
            if (request.hasAnnotation()) {
                event.setAnnotation(request.getAnnotation());
            }
            if (request.hasCategory()) {
                Category category = findCategoryById(request.getCategory());
                event.setCategory(category);
            }
            if (request.hasDescription()) {
                event.setDescription(request.getDescription());
            }
            if (request.hasLocation()) {
                Location requestLocation = LocationMapper.toLocation(request.getLocation());
                if (!requestLocation.equals(event.getLocation())) {
                    Location location = locationRepository.save(requestLocation);
                    event.setLocation(location);
                }
            }
            if (request.hasPaid()) {
                event.setPaid(request.hasPaid());
            }
            if (request.hasParticipantLimit()) {
                event.setParticipantLimit(request.getParticipantLimit());
            }
            if (request.hasRequestModeration()) {
                event.setRequestModeration(request.getRequestModeration());
            }
            if (request.hasStateAction()) {
                EventStateAction eventStateAction = request.getStateAction();

                switch (eventStateAction) {
                    case SEND_TO_REVIEW -> event.setEventState(EventState.PENDING);
                    case CANCEL_REVIEW -> event.setEventState(EventState.CANCELED);
                }
            }
            if (request.hasTitle()) {
                event.setTitle(request.getTitle());
            }

            List<Comment> comments = commentRepository.findAllByEventIdAndStatus(event.getId());
            return EventMapper.toEventFullDto(event, comments);
        }
        throw new ValidationConflict("Событие не удовлетворяет правилам редактирования");
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByEventOwnerId(long userId, long eventId) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        List<Request> requests = requestRepository.findByEventId(event.getId());
        return RequestMapper.toListOfParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeParticipationRequestStatus(long userId,
                                                                           long eventId,
                                                                           EventRequestStatusUpdateRequest request) {
        List<Request> requests = requestRepository.findAllById(request.getRequestIds());

        if (requests.isEmpty()) {
            return new EventRequestStatusUpdateResult(
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        long participantLimit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();
        long alreadyConfirmedRequests = event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests();

        if (participantLimit == 0 || !event.getRequestModeration()) {
            throw new ValidationException("Подтверждение/отклонение заявки для данного события не требуется");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        RequestStatus requestStatus = request.getStatus();

        switch (requestStatus) {
            case CONFIRMED -> {
                for (Request r : requests) {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ValidationException("Текущий запрос должен иметь статус PENDING");
                    }
                    if (participantLimit > 0 && alreadyConfirmedRequests >= participantLimit) {
                        throw new ValidationConflict("У события достигнут лимит запросов на участие");
                    } else {
                        r.setStatus(RequestStatus.CONFIRMED);
                        requestRepository.save(r);
                        confirmedRequests.add(r);
                        alreadyConfirmedRequests++;
                    }
                }
                event.setConfirmedRequests(alreadyConfirmedRequests);
                eventRepository.save(event);
            }
            case REJECTED -> {
                for (Request r : requests) {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ValidationConflict("Текущий запрос должен иметь статус PENDING");
                    }
                    r.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(r);
                    rejectedRequests.add(r);
                }
            }
            default -> throw new ValidationConflict("Невалидный статус");
        }

        List<ParticipationRequestDto> confirmedRequestsDto = RequestMapper.toListOfParticipationRequestDto(confirmedRequests);
        List<ParticipationRequestDto> rejectedRequestsDto = RequestMapper.toListOfParticipationRequestDto(rejectedRequests);

        return new EventRequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }

    @Override
    public List<CommentDto> getAllCommentsByEventOwnerId(long userId, long eventId) {
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return CommentMapper.toListOfCommentDto(comments);
    }

    @Override
    @Transactional
    public EventCommentStatusUpdateResult changeCommentStatus(long userId,
                                                              long eventId,
                                                              EventCommentStatusUpdateRequest request) {
        List<Comment> comments = commentRepository.findAllById(request.getCommentIds());

        if (comments.isEmpty()) {
            return new EventCommentStatusUpdateResult(
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        List<Comment> confirmedComments = new ArrayList<>();
        List<Comment> rejectedComments = new ArrayList<>();

        CommentStatus commentStatus = request.getStatus();

        switch (commentStatus) {
            case CONFIRMED -> {
                for (Comment c : comments) {
                    if (!c.getStatus().equals(CommentStatus.PENDING)) {
                        throw new ValidationConflict("Текущий комментарий должен иметь статус PENDING");
                    }
                    c.setStatus(CommentStatus.CONFIRMED);
                    commentRepository.save(c);
                    confirmedComments.add(c);
                }
            }
            case REJECTED -> {
                for (Comment c : comments) {
                    if (!c.getStatus().equals(CommentStatus.PENDING)) {
                        throw new ValidationConflict("Текущий комментарий должен иметь статус PENDING");
                    }
                    c.setStatus(CommentStatus.REJECTED);
                    commentRepository.save(c);
                    rejectedComments.add(c);
                }
            }
            default -> throw new ValidationConflict("Невалидный статус");
        }

        List<CommentDto> confirmedCommentsDt = CommentMapper.toListOfCommentDto(confirmedComments);
        List<CommentDto> rejectedCommentsDt = CommentMapper.toListOfCommentDto(rejectedComments);

        return new EventCommentStatusUpdateResult(confirmedCommentsDt, rejectedCommentsDt);
    }

    // -------- PUBLIC --------

    @Override
    public List<EventShortDto> getPublicEventsWithParams(String text,
                                                         List<Integer> categories,
                                                         Boolean paid,
                                                         LocalDateTime rangeStart,
                                                         LocalDateTime rangeEnd,
                                                         Boolean onlyAvailable,
                                                         String sort,
                                                         int from,
                                                         int size,
                                                         HttpServletRequest request) {
        statsClient.createHit(CreateHitRequest.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException(String.format("Дата начала %s не может быть позже даты окончания %s",
                    rangeStart, rangeEnd));
        }

        String searchText = (text != null) ? text.toLowerCase() : null;
        Pageable pageable = null;

        if (sort == null) {
            pageable = PageRequest.of(from / size, size);
        } else if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
        } else if ("VIEWS".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("views").ascending());
        }

        List<Event> eventList;

        if (rangeStart != null && rangeEnd != null) {
            eventList = eventRepository.findAllEventsForPublicByParams(searchText, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, pageable);
        } else {
            eventList = eventRepository.findAllEventsForPublicByParamsWithoutDate(searchText, categories, paid,
                    LocalDateTime.now(), onlyAvailable, pageable);
        }
        return EventMapper.toListOfEventShortDto(eventList);
    }

    @Override
    public EventFullDto getPublicEventById(long eventId, HttpServletRequest request) {
        statsClient.createHit(CreateHitRequest.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Event event = eventRepository.findByIdWithCategoryAndInitiatorAndLocation(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (!event.getEventState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id = %d не опубликовано", eventId));
        }

        long views = event.getViews() == null ? 0 : event.getViews();

        event.setViews(views + 1);
        eventRepository.save(event);

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(event.getId());
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, comments);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }

    // -------- ADMIN --------

    @Override
    public List<EventFullDto> getAdminEventsWithParams(List<Integer> users,
                                                       List<String> states,
                                                       List<Integer> categories,
                                                       LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       int from,
                                                       int size) {
        if (rangeStart != null && rangeEnd != null && (rangeEnd.isBefore(rangeStart))) {
            throw new ValidationException(String.format("Дата начала %s не может быть позже даты окончания %s",
                    rangeStart, rangeEnd));
        }

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> eventList;

        if (rangeStart == null || rangeEnd == null) {
            eventList = eventRepository.findAllEventsForAdminByParamsWithoutDate(users, states, categories, pageable);
        } else {
            eventList = eventRepository.findAllEventsForAdminByParams(users, states, categories, rangeStart, rangeEnd, pageable);
        }
        List<Long> eventIds = eventList.stream().map(Event::getId).toList();
        List<Comment> comments = commentRepository.findAllByEventIdInWithEvents(eventIds);

        Map<Long, List<Comment>> commentsByEventId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getEvent().getId()));

        return EventMapper.toListOfEventFullDto(eventList, commentsByEventId);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findByIdWithCategoryAndInitiatorAndLocation(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (event.getEventDate() != null) {
            Duration duration = Duration.between(LocalDateTime.now(), event.getEventDate());

            if (duration.toHours() < 1) {
                throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от " +
                        "даты публикации");
            }
        }
        if (request.hasAnnotation()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.hasCategory()) {
            Category category = findCategoryById(request.getCategory());
            event.setCategory(category);
        }
        if (request.hasDescription()) {
            event.setDescription(request.getDescription());
        }
        if (request.hasEventDate()) {
            Duration duration = Duration.between(LocalDateTime.now(), request.getEventDate());

            if (duration.toHours() < 1) {
                throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от " +
                        "даты публикации");
            }
            event.setEventDate(request.getEventDate());
        }
        if (request.hasLocation()) {
            Location requestLocation = LocationMapper.toLocation(request.getLocation());
            if (!requestLocation.equals(event.getLocation())) {
                Location location = locationRepository.save(requestLocation);
                event.setLocation(location);
            }
        }
        if (request.hasPaid()) {
            event.setPaid(request.getPaid());
        }
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.hasRequestModeration()) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.hasStateAction()) {
            EventStateAction eventStateAction = request.getStateAction();

            switch (eventStateAction) {
                case PUBLISH_EVENT -> {
                    if (!event.getEventState().equals(EventState.PENDING)) {
                        throw new ValidationConflict("Событие можно опубликовать только если оно " +
                                "в состоянии ожидания публикации");
                    }
                    event.setEventState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getEventState().equals(EventState.PUBLISHED)) {
                        throw new ValidationConflict("Событие можно отклонить только если оно еще не опубликовано");
                    }
                    event.setEventState(EventState.REJECTED);
                }
                default -> {
                }
            }
        }
        if (request.hasTitle()) {
            event.setTitle(request.getTitle());
        }
        List<Comment> comments = commentRepository.findByEventId(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    private Category findCategoryById(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id = %d не найдена", catId)));
    }
}