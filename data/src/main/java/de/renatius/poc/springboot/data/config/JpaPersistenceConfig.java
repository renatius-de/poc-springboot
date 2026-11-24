package de.renatius.poc.springboot.data.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@Profile("postgresql | (!mongodb & !postgresql)")
@EntityScan(basePackages = "de.renatius.poc.springboot.data.entity")
@EnableJpaRepositories(basePackages = "de.renatius.poc.springboot.data.repository")
public class JpaPersistenceConfig {}
