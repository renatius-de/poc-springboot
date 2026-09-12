package de.renatius.poc.springboot.data.repository;

import de.renatius.poc.springboot.data.entity.Professor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfessorRepository
    extends JpaRepository<Professor, UUID>, JpaSpecificationExecutor<Professor> {}
