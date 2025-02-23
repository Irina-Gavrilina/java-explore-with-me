package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {


    @Query("""
            SELECT r FROM Request AS r
            JOIN FETCH r.requester AS u
            WHERE r.requester.id = :requesterId
            AND r.event.id = :eventId
            """)
    Optional<Request> findByRequesterIdAndEventIdWithRequester(@Param("requesterId") long requesterId,
                                                               @Param("eventId") long eventId);

    Boolean existsByRequesterIdAndEventId(long userId, long eventId);

    List<Request> findByRequesterId(long requesterId);

    List<Request> findByEventId(long eventId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);
}