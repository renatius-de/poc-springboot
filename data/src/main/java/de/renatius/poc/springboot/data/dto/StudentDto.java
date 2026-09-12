package de.renatius.poc.springboot.data.dto;

import java.util.UUID;

public record StudentDto(UUID id, String firstName, String lastName) {}
