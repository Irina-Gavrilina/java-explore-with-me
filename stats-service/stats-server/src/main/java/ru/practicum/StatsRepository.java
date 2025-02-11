package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("""
            SELECT new ru.practicum.StatsResponse(s.app, s.uri, COUNT(DISTINCT s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<StatsResponse> getStatsWhenUniqueIpsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            SELECT new ru.practicum.StatsResponse(s.app, s.uri, COUNT(DISTINCT s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<StatsResponse> getStatsWhenUniqueIpsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.StatsResponse(s.app, s.uri, COUNT(s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
            GROUP BY s.app, s.uri
            ORDER BY COUNT (s.ip) DESC
            """)
    List<StatsResponse> getStatsWhenAllIpsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            SELECT new ru.practicum.StatsResponse(s.app, s.uri, COUNT(s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.app, s.uri
            ORDER BY COUNT (s.ip) DESC
            """)
    List<StatsResponse> getStatsWhenAllIpsWithoutUris(LocalDateTime start, LocalDateTime end);
}