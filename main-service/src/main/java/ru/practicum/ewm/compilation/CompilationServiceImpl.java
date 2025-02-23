package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    // -------- ADMIN --------

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events;

        if (CollectionUtils.isNotEmpty(newCompilationDto.getEvents())) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        } else {
            events = new HashSet<>(Collections.emptySet());
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка событий с id = %d не найдена", compId)));

        if (request.hasEvents()) {
            if (request.getEvents().isEmpty()) {
                throw new ValidationException("Список id событий пуст");
            }
            Set<Event> events = new HashSet<>(eventRepository.findAllById(request.getEvents()));
            compilation.setEvents(events);
        }
        if (request.hasPinned()) {
            compilation.setPinned(request.getPinned());
        }
        if (request.hasTitle()) {
            compilation.setTitle(request.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка событий с id = %d не найдена", compId)));
        compilationRepository.deleteById(compilation.getId());
    }

    // -------- PUBLIC --------

    @Override
    public List<CompilationDto> getCompilationsByParams(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findByParams(pinned, pageable);
        return CompilationMapper.toListOfCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка событий с id = %d не найдена", compId)));
        return CompilationMapper.toCompilationDto(compilation);
    }
}