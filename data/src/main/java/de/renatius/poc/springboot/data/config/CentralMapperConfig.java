package de.renatius.poc.springboot.data.config;

import org.mapstruct.BeanMapping;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CentralMapperConfig {

  @BeanMapping(ignoreByDefault = true)
  default Object prototype(Object source) {
    return source;
  }
}
