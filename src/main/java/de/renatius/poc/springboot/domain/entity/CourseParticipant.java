package de.renatius.poc.springboot.domain.entity;

import de.renatius.poc.springboot.persistence.entity.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public record CourseParticipant(@NotNull UUID id,
                                @NotBlank @Size(min = 7, max = 7) String matriculationNumber,
                                @NotBlank @Size(min = 1, max = 1024) String firstname,
                                String middleName,
                                @NotBlank @Size(min = 1, max = 1024) String lastname,
                                @NotNull OffsetDateTime createdAt,
                                @NotNull OffsetDateTime updatedAt,
                                @NotNull Set<Course> courses) {
    public CourseParticipant(final UUID id,
                             final String matriculationNumber,
                             final String firstname,
                             final String middleName,
                             final String lastname,
                             final OffsetDateTime createdAt,
                             final OffsetDateTime updatedAt) {
        this(id, matriculationNumber, firstname, middleName, lastname, createdAt, updatedAt, new LinkedHashSet<>());
    }

    public CourseParticipant(final UUID id,
                             final String matriculationNumber,
                             final String firstname,
                             final String middleName,
                             final String lastname,
                             final OffsetDateTime createdAt) {
        this(id, matriculationNumber, firstname, middleName, lastname, createdAt, OffsetDateTime.now());
    }

    public CourseParticipant(final UUID id,
                             final String matriculationNumber,
                             final String firstname,
                             final String middleName,
                             final String lastname) {
        this(id, matriculationNumber, firstname, middleName, lastname, OffsetDateTime.now());
    }

    public CourseParticipant(final String matriculationNumber,
                             final String firstname,
                             final String middleName,
                             final String lastname) {
        this(UUID.randomUUID(), matriculationNumber, firstname, middleName, lastname);
    }
}
