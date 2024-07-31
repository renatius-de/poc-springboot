package de.renatius.poc.springboot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "uid", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 1023)
    private String title;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "professors_holds_courses",
            joinColumns = @JoinColumn(name = "course_uid"),
            inverseJoinColumns = @JoinColumn(name = "professor_uid"))
    private Set<Professor> professors = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "students_takes_part_in_courses",
            joinColumns = @JoinColumn(name = "course_uid"),
            inverseJoinColumns = @JoinColumn(name = "student_uid"))
    private Set<Student> students = new LinkedHashSet<>();

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Course course)) {
            return false;
        }

        return id.equals(course.id)
                && title.equals(course.title)
                && Objects.equals(createdAt, course.createdAt)
                && Objects.equals(updatedAt, course.updatedAt);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + Objects.hashCode(createdAt);
        result = 31 * result + Objects.hashCode(updatedAt);

        return result;
    }
}
