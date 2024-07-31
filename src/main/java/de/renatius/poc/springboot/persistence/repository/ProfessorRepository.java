package de.renatius.poc.springboot.persistence.repository;

import de.renatius.poc.springboot.persistence.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
}
