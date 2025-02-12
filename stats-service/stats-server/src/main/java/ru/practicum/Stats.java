package ru.practicum;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "app", nullable = false, length = 512)
    String app;
    @Column(name = "uri", nullable = false, length = 512)
    String uri;
    @Column(name = "ip", nullable = false, length = 45)
    String ip;
    @Column(name = "created_at", nullable = false)
    LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stats)) return false;
        return id != null && id.equals(((Stats) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}