package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitResponse createHit(CreateHitRequest request);

    List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}