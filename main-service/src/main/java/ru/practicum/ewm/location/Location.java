package ru.practicum.ewm.location;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "lat", nullable = false)
    Float lat;

    @Column(name = "lon", nullable = false)
    Float lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        return id != null && id.equals(((Location) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}