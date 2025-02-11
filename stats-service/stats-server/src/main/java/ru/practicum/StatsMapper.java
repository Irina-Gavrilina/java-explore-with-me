package ru.practicum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static ru.practicum.client.constants.Constants.DATE_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsMapper {

    public static HitResponse toHitResponse(Stats stats) {
        return HitResponse.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .uri(stats.getUri())
                .ip(stats.getApp())
                .timestamp(DATE_FORMATTER.format(stats.getTimestamp()))
                .build();
    }

    public static Stats toStats(CreateHitRequest request) {
        return Stats.builder()
                .app(request.getApp())
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(request.getTimestamp())
                .build();
    }
}