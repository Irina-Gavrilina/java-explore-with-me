package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    void deleteCompilation(long compId);

    List<CompilationDto> getCompilationsByParams(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);
}