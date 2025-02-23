package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u FROM User u
            WHERE u.id IN :ids
            """)
    List<User> findUsersByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT u FROM User u")
    List<User> findAllUsers(Pageable pageable);
}