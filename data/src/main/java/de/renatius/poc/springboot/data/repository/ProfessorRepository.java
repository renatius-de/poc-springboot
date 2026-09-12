package de.renatius.poc.springboot.data.repository;

import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {}
