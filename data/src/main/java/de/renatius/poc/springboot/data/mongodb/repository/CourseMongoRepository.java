package de.renatius.poc.springboot.data.mongodb.repository;

import de.renatius.poc.springboot.data.mongodb.document.CourseDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseMongoRepository extends MongoRepository<CourseDocument, UUID> {}
