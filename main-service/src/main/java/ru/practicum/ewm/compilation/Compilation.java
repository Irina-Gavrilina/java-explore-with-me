package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.Event;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @ManyToMany
    @JoinTable(name = "compilations_to_event", joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
    Boolean pinned;
    @Column(name = "title", nullable = false, length = 50)
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compilation)) return false;
        return id != null && id.equals(((Compilation) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}