package ru.practicum.ewm.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndAuthorId(long commentId, long authorId);

    List<Comment> findAllByAuthorId(long userId);

    List<Comment> findByEventId(long eventId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.event AS e
            JOIN FETCH c.author AS a
            WHERE c.event.id = :eventId AND c.status = 'CONFIRMED'
            """)
    List<Comment> findByEventId(long eventId, Pageable pageable);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.event AS e
            JOIN FETCH c.author AS a
            WHERE (:text IS NULL OR (c.text ILIKE %:text%))
            """)
    List<Comment> findAllForAdminByParams(@Param("text") String text, Pageable pageable);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.event AS e
            JOIN FETCH c.author AS a
            WHERE c.event.id = :eventId AND c.status = 'CONFIRMED'
            """)
    List<Comment> findAllByEventIdAndStatus(@Param("eventId") long eventId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.event AS e
            JOIN FETCH c.author AS a
            WHERE c.event.id IN :eventIds
            """)
    List<Comment> findAllByEventIdInWithEvents(@Param("eventIds")List<Long> eventIds);
}