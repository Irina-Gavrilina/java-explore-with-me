package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e
            FROM Event AS e
            JOIN FETCH e.category AS c
            WHERE e.initiator.id = :initiatorId
            """)
    List<Event> findByInitiatorIdWithCategory(@Param("initiatorId") long initiatorId, Pageable pageable);

    @Query("""
            SELECT e
            FROM Event AS e
            JOIN FETCH e.category AS c
            JOIN FETCH e.location AS l
            WHERE e.id = :eventId
            AND e.initiator.id = :initiatorId
            """)
    Optional<Event> findByIdAndInitiatorIdWithCategoryAndLocation(@Param("eventId") long eventId,
                                                                  @Param("initiatorId") long initiatorId);

    @Query("""
            SELECT e
            FROM Event AS e
            JOIN FETCH e.initiator AS i
            WHERE e.id = :eventId
            """)
    Optional<Event> findByIdWithInitiator(@Param("eventId") long eventId);

    @Query("""
            SELECT e
            FROM Event AS e
            JOIN FETCH e.category AS c
            JOIN FETCH e.initiator AS i
            JOIN FETCH e.location AS l
            WHERE e.id = :eventId
            """)
    Optional<Event> findByIdWithCategoryAndInitiatorAndLocation(@Param("eventId") long eventId);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category AS c
            JOIN FETCH e.initiator AS i
            JOIN FETCH e.location AS l
            WHERE (:users IS NULL OR e.initiator.id IN :users)
            AND (:states IS NULL OR e.eventState IN :states)
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)
            ORDER BY e.views DESC
            """)
    List<Event> findAllEventsForAdminByParams(@Param("users") List<Integer> users,
                                              @Param("states") List<String> states,
                                              @Param("categories") List<Integer> categories,
                                              @Param("rangeStart") LocalDateTime rangeStart,
                                              @Param("rangeEnd") LocalDateTime rangeEnd,
                                              Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category AS c
            JOIN FETCH e.initiator AS i
            JOIN FETCH e.location AS l
            WHERE (:users IS NULL OR e.initiator.id IN :users)
            AND (:states IS NULL OR e.eventState IN :states)
            AND (:categories IS NULL OR e.category.id IN :categories)
            ORDER BY e.views DESC
            """)
    List<Event> findAllEventsForAdminByParamsWithoutDate(@Param("users") List<Integer> users,
                                                         @Param("states") List<String> states,
                                                         @Param("categories") List<Integer> categories,
                                                         Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            JOIN FETCH e.category AS c
            JOIN FETCH e.initiator AS i
            JOIN FETCH e.location AS l
            WHERE (:text IS NULL OR (e.annotation ILIKE %:text% OR e.description ILIKE %:text%))
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)
            AND (:onlyAvailable = FALSE OR e.participantLimit > e.confirmedRequests)
            AND e.eventState = 'PUBLISHED'
            """)
    List<Event> findAllEventsForPublicByParams(@Param("text") String text,
                                               @Param("categories") List<Integer> categories,
                                               @Param("paid") Boolean paid,
                                               @Param("rangeStart") LocalDateTime rangeStart,
                                               @Param("rangeEnd") LocalDateTime rangeEnd,
                                               @Param("onlyAvailable") Boolean onlyAvailable,
                                               Pageable pageable);

    @Query("""
           SELECT e FROM Event e
           JOIN FETCH e.category AS c
           JOIN FETCH e.initiator AS i
           JOIN FETCH e.location AS l
           WHERE (:text IS NULL OR (e.annotation ILIKE %:text% OR e.description ILIKE %:text%))
           AND (:categories IS NULL OR e.category.id IN :categories)
           AND (:paid IS NULL OR e.paid = :paid)
           AND e.eventDate > :now
           AND (:onlyAvailable = FALSE OR e.participantLimit > e.confirmedRequests)
           AND e.eventState = 'PUBLISHED'
           """)
    List<Event> findAllEventsForPublicByParamsWithoutDate (@Param("text") String text,
                                                           @Param("categories") List<Integer> categories,
                                                           @Param("paid") Boolean paid,
                                                           @Param("now") LocalDateTime now,
                                                           @Param("onlyAvailable") Boolean onlyAvailable,
                                                           Pageable pageable);

    List<Event> findByCategoryId(long catId);

    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);
}