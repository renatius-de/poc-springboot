package de.renatius.poc.springboot.data.mongodb.repository;

import de.renatius.poc.springboot.data.mongodb.document.ProfessorDocument;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessorMongoRepository extends MongoRepository<ProfessorDocument, UUID> {}
