package ru.practicum;

import ru.practicum.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitResponse createHit(CreateHitRequest request) {
        Stats stats = StatsMapper.toStats(request);
        return StatsMapper.toHitResponse(statsRepository.save(stats));
    }

    @Override
    public List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException(String.format("Дата начала %s не может быть позже даты окончания %s", start, end));
        }
        if (unique) {
            if (CollectionUtils.isNotEmpty(uris)) {
                return statsRepository.getStatsWhenUniqueIpsWithUris(start, end, uris);
            }
            return statsRepository.getStatsWhenUniqueIpsWithoutUris(start, end);
        } else {
            if (CollectionUtils.isNotEmpty(uris)) {
                return statsRepository.getStatsWhenAllIpsWithUris(start, end, uris);
            }
            return statsRepository.getStatsWhenAllIpsWithoutUris(start, end);
        }
    }
}