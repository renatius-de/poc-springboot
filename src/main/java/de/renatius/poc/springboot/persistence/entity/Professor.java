package de.renatius.poc.springboot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "professors")
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "uid", nullable = false)
    private UUID id;

    @Column(name = "firstname", nullable = false, length = 1023)
    private String firstname;

    @Column(name = "middle_name", length = 1023)
    private String middleName;

    @Column(name = "surname", nullable = false, length = 1023)
    private String surname;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ToString.Exclude
    @ManyToMany(mappedBy = "professors")
    private Set<Course> courses = new LinkedHashSet<>();

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final Professor professor)) {
            return false;
        }

        return id.equals(professor.id)
                && firstname.equals(professor.firstname)
                && Objects.equals(middleName, professor.middleName)
                && surname.equals(professor.surname)
                && createdAt.equals(professor.createdAt)
                && updatedAt.equals(professor.updatedAt);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstname.hashCode();
        result = 31 * result + Objects.hashCode(middleName);
        result = 31 * result + surname.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + updatedAt.hashCode();

        return result;
    }
}
