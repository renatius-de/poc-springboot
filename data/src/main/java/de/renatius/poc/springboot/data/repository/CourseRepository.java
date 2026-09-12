package de.renatius.poc.springboot.data.repository;

import de.renatius.poc.springboot.data.entity.Course;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository
    extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {}
