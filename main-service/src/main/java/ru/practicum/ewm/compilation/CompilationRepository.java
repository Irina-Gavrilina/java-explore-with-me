package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("""
            SELECT c FROM Compilation AS c
            WHERE (:pinned IS NULL OR c.pinned = :pinned)
            """)
    List<Compilation> findByParams(@Param("pinned") Boolean pinned, Pageable pageable);
}