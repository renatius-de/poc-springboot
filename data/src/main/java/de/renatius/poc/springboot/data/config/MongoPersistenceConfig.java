package de.renatius.poc.springboot.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration(proxyBeanMethods = false)
@Profile("mongodb")
@EnableMongoRepositories(basePackages = "de.renatius.poc.springboot.data.mongodb.repository")
public class MongoPersistenceConfig {}
