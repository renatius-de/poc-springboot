package de.renatius.poc.springboot.data.repository;

import de.renatius.poc.springboot.data.entity.Student;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository
    extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {}
