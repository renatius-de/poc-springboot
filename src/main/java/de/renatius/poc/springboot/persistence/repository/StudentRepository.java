package de.renatius.poc.springboot.persistence.repository;

import de.renatius.poc.springboot.persistence.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}
