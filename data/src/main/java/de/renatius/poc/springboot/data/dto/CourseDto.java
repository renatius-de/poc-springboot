package de.renatius.poc.springboot.data.dto;

import java.util.UUID;

public record CourseDto(UUID id, String name, String room, UUID professorId) {}
