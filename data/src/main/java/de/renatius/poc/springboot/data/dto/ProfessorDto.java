package de.renatius.poc.springboot.data.dto;

import java.util.UUID;

public record ProfessorDto(UUID id, String title, String firstName, String lastName) {}
