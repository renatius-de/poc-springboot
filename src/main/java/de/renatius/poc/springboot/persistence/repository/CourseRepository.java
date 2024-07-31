package de.renatius.poc.springboot.persistence.repository;

import de.renatius.poc.springboot.persistence.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
