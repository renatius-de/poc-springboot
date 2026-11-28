package de.renatius.poc.springboot.data.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.TestDataApplication;
import de.renatius.poc.springboot.data.mongodb.document.CourseDocument;
import de.renatius.poc.springboot.data.mongodb.document.ProfessorDocument;
import de.renatius.poc.springboot.data.mongodb.document.StudentDocument;
import de.renatius.poc.springboot.data.mongodb.repository.CourseMongoRepository;
import de.renatius.poc.springboot.data.mongodb.repository.ProfessorMongoRepository;
import de.renatius.poc.springboot.data.mongodb.repository.StudentMongoRepository;
import de.renatius.poc.springboot.testsupport.AbstractPostgresMongoTestcontainersTest;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestDataApplication.class)
@ActiveProfiles("mongodb")
class MongoRepositoryIT extends AbstractPostgresMongoTestcontainersTest {

    @Autowired
    private ProfessorMongoRepository professorRepository;
    @Autowired
    private CourseMongoRepository courseRepository;
    @Autowired
    private StudentMongoRepository studentRepository;

    @BeforeEach
    void clearRepositories() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        professorRepository.deleteAll();
    }

    @Test
    void shouldPersistAndReadDocumentsWithUuidIdsAndRelationships() {
        UUID professorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        professorRepository.save(
                new ProfessorDocument(professorId, "Dr.", "Ada", "Lovelace"));
        courseRepository.save(new CourseDocument(courseId, "Algorithms", "A-101", professorId));
        studentRepository.save(
                new StudentDocument(studentId, "Alan", "Turing", Set.of(courseId)));

        assertThat(professorRepository.findById(professorId))
                .hasValueSatisfying(
                        professor -> assertThat(professor.getLastName()).isEqualTo("Lovelace"));
        assertThat(courseRepository.findById(courseId))
                .hasValueSatisfying(course -> assertThat(course.getProfessorId()).isEqualTo(professorId));
        assertThat(studentRepository.findById(studentId))
                .hasValueSatisfying(student -> assertThat(student.getCourseIds()).isEqualTo(Set.of(courseId)));
    }
}
