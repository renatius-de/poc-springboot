package com.example.data.repository;

import com.example.data.entity.Professor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {}
