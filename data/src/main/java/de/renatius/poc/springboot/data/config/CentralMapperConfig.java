package de.renatius.poc.springboot.data.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMappingStrategy = NullValueMappingStrategy.EXCEPTION,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.EXCEPTION,
        nullValueMapMappingStrategy = NullValueMappingStrategy.EXCEPTION,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        ignoreByDefault = true)
public interface CentralMapperConfig {
}
