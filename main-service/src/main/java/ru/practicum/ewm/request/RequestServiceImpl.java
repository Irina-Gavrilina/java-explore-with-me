package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationConflict;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAllRequestsByUserId(long userId) {
        User user = checkUserExists(userId);
        List<Request> requests = requestRepository.findByRequesterId(user.getId());
        return RequestMapper.toListOfParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        log.info("Начало выполнения метода createRequest для userId = {}, eventId = {}", userId, eventId);
        User user = checkUserExists(userId);
        Event event = eventRepository.findByIdWithInitiator(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationConflict("Нельзя добавить повторный запрос на участие в событии");
        }
        if (Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new ValidationConflict("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ValidationConflict("Нельзя участвовать в неопубликованном событии");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        long participantLimit = (event.getParticipantLimit() == null ? 0 : event.getParticipantLimit());
        long confirmedRequests = (event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests());

        if (participantLimit > 0) {
            if (confirmedRequests >= participantLimit) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                throw new ValidationConflict("У события достигнут лимит запросов на участие");
            }
        }
        if (event.getRequestModeration()) {
            if (participantLimit == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(confirmedRequests + 1);
                eventRepository.save(event);
            } else {
                request.setStatus(RequestStatus.PENDING);
            }
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests + 1);
            eventRepository.save(event);
        }
        log.info("Завершение выполнения метода createRequest для userId = {}, eventId = {}", userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = checkUserExists(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id = %d не найден", requestId)));

        if (request.getRequester().getId() != userId) {
            throw new ValidationConflict("Отменить свой запрос на участие в событии может только его создатель");
        }

        if (request.getStatus().equals(RequestStatus.CANCELED)
                || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ValidationConflict("Данный запрос уже отменен/отклонен");
        }

        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            request.setStatus(RequestStatus.CANCELED);
            Event event = eventRepository.findById(request.getId()).orElseThrow(() ->
                    new NotFoundException(String.format("Событие с id = %d не найдено", request.getId())));
            if (event.getConfirmedRequests() > 0) {
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                eventRepository.save(event);
            }
        } else {
            request.setStatus(RequestStatus.CANCELED);
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private User checkUserExists(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }
}