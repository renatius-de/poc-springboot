package de.renatius.poc.springboot.data.mongodb.repository;

import de.renatius.poc.springboot.data.mongodb.document.StudentDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentMongoRepository extends MongoRepository<StudentDocument, UUID> {}
