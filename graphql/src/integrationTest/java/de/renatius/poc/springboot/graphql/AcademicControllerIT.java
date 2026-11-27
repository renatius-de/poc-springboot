package de.renatius.poc.springboot.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import de.renatius.poc.springboot.data.entity.Course;
import de.renatius.poc.springboot.data.entity.Professor;
import de.renatius.poc.springboot.data.repository.CourseRepository;
import de.renatius.poc.springboot.data.repository.ProfessorRepository;
import de.renatius.poc.springboot.data.repository.StudentRepository;
import de.renatius.poc.springboot.graphql.testsupport.AbstractTestcontainersTest;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
class AcademicControllerIT extends AbstractTestcontainersTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private CourseRepository courseRepository;

    private GraphQlTester graphQlTester;

    @BeforeEach
    void configureGraphQlTester() {
        WebTestClient webTestClient = MockMvcWebTestClient
                .bindToApplicationContext(webApplicationContext)
                .build();
        graphQlTester = HttpGraphQlTester
                .builder(webTestClient.mutate())
                .url("/graphql")
                .build();
    }

    @Test
    void shouldCreateQuerySearchUpdateAndDeleteStudent() {
        String firstName = "Ada-" + UUID.randomUUID();

        String id = graphQlTester
                .document("""
                    mutation ($input: StudentInput!) {
                    createStudent(input: $input) { id firstName lastName }
                    }
                    """)
                .variable("input", Map.of("firstName", firstName, "lastName", "Lovelace"))
                .execute()
                .path("createStudent.id")
                .entity(String.class)
                .get();

        UUID studentId = UUID.fromString(id);
        assertThat(studentRepository.findById(studentId))
                .get()
                .satisfies(student -> assertThat(student.getFirstName()).isEqualTo(firstName));

        graphQlTester
                .document("query ($id: ID!) { student(id: $id) { id firstName lastName } }")
                .variable("id", id)
                .execute()
                .path("student.lastName")
                .entity(String.class)
                .isEqualTo("Lovelace");

        graphQlTester
                .document("""
                    query ($firstName: String!, $page: Int!, $size: Int!) {
                        students(firstName: $firstName, page: $page, size: $size) {
                            content { id firstName }
                            totalElements page size
                        }
                    }
                    """)
                .variable("firstName", firstName)
                .variable("page", 0)
                .variable("size", 5)
                .execute()
                .path("students.content[0].id")
                .entity(String.class)
                .isEqualTo(id);

        graphQlTester
                .document("""
                    mutation ($id: ID!, $input: StudentInput!) {
                        updateStudent(id: $id, input: $input) { id firstName lastName }
                        }
                    """)
                .variable("id", id)
                .variable("input", Map.of("firstName", "Augusta", "lastName", "Lovelace"))
                .execute()
                .path("updateStudent.firstName")
                .entity(String.class)
                .isEqualTo("Augusta");

        assertThat(studentRepository.findById(studentId))
                .get()
                .satisfies(student -> assertThat(student.getFirstName()).isEqualTo("Augusta"));

        graphQlTester
                .document("mutation ($id: ID!) { deleteStudent(id: $id) }")
                .variable("id", id)
                .execute()
                .path("deleteStudent")
                .entity(Boolean.class)
                .isEqualTo(true);

        assertThat(studentRepository.findById(studentId)).isEmpty();
    }

    @Test
    void shouldCreateQuerySearchUpdateAndDeleteProfessor() {
        String firstName = "Grace-" + UUID.randomUUID();

        String id = graphQlTester
                .document("""
                    mutation ($input: ProfessorInput!) {
                    createProfessor(input: $input) { id title firstName lastName }
                    }
                    """)
                .variable(
                        "input",
                        Map.of("title", "Dr.", "firstName", firstName, "lastName", "Hopper"))
                .execute()
                .path("createProfessor.id")
                .entity(String.class)
                .get();

        UUID professorId = UUID.fromString(id);
        assertThat(professorRepository.findById(professorId))
                .get()
                .satisfies(professor -> assertThat(professor.getLastName()).isEqualTo("Hopper"));

        graphQlTester
                .document("query ($id: ID!) { professor(id: $id) { id title firstName lastName } }")
                .variable("id", id)
                .execute()
                .path("professor.title")
                .entity(String.class)
                .isEqualTo("Dr.");

        graphQlTester
                .document("""
                    query ($firstName: String!, $page: Int!, $size: Int!) {
                        professors(firstName: $firstName, page: $page, size: $size) {
                            content { id firstName }
                            totalElements page size
                        }
                    }
                    """)
                .variable("firstName", firstName)
                .variable("page", 0)
                .variable("size", 5)
                .execute()
                .path("professors.content[0].id")
                .entity(String.class)
                .isEqualTo(id);

        graphQlTester
                .document("""
                    mutation ($id: ID!, $input: ProfessorInput!) {
                        updateProfessor(id: $id, input: $input) { id title firstName lastName }
                    }
                    """)
                .variable("id", id)
                .variable(
                        "input",
                        Map.of("title", "Prof.", "firstName", firstName, "lastName", "Hopper"))
                .execute()
                .path("updateProfessor.title")
                .entity(String.class)
                .isEqualTo("Prof.");

        assertThat(professorRepository.findById(professorId))
                .get()
                .satisfies(professor -> assertThat(professor.getTitle()).isEqualTo("Prof."));

        graphQlTester
                .document("mutation ($id: ID!) { deleteProfessor(id: $id) }")
                .variable("id", id)
                .execute()
                .path("deleteProfessor")
                .entity(Boolean.class)
                .isEqualTo(true);

        assertThat(professorRepository.findById(professorId)).isEmpty();
    }

    @Test
    void shouldCreateQuerySearchUpdateAndDeleteCourse() {
        Professor professor = professorRepository
                .save(
                        Professor
                                .builder()
                                .title("Dr.")
                                .firstName("Barbara")
                                .lastName("Liskov")
                                .build());
        String courseName = "Type Systems-" + UUID.randomUUID();

        String id = graphQlTester
                .document("""
                    mutation ($input: CourseInput!) {
                        createCourse(input: $input) { id name room professorId }
                    }
                    """)
                .variable(
                        "input",
                        Map.of(
                                "name", courseName,
                                "room", "A-101",
                                "professorId", professor.getId().toString()))
                .execute()
                .path("createCourse.id")
                .entity(String.class)
                .get();

        UUID courseId = UUID.fromString(id);
        Course course = courseRepository.findById(courseId).orElseThrow();
        assertThat(course.getProfessor().getId()).isEqualTo(professor.getId());

        graphQlTester
                .document("query ($id: ID!) { course(id: $id) { id name room professorId } }")
                .variable("id", id)
                .execute()
                .path("course.professorId")
                .entity(String.class)
                .isEqualTo(professor.getId().toString());

        graphQlTester
                .document("""
                    query ($name: String!, $page: Int!, $size: Int!) {
                        courses(name: $name, page: $page, size: $size) {
                            content { id name }
                            totalElements page size
                        }
                    }
                    """)
                .variable("name", courseName)
                .variable("page", 0)
                .variable("size", 5)
                .execute()
                .path("courses.content[0].id")
                .entity(String.class)
                .isEqualTo(id);

        graphQlTester
                .document("""
                    mutation ($id: ID!, $input: CourseInput!) {
                        updateCourse(id: $id, input: $input) { id name room professorId }
                    }
                    """)
                .variable("id", id)
                .variable(
                        "input",
                        Map.of(
                                "name", "Advanced " + courseName,
                                "room", "B-202",
                                "professorId", professor.getId().toString()))
                .execute()
                .path("updateCourse.name")
                .entity(String.class)
                .isEqualTo("Advanced " + courseName);

        assertThat(courseRepository.findById(courseId).orElseThrow().getName())
                .isEqualTo("Advanced " + courseName);

        graphQlTester
                .document("mutation ($id: ID!) { deleteCourse(id: $id) }")
                .variable("id", id)
                .execute()
                .path("deleteCourse")
                .entity(Boolean.class)
                .isEqualTo(true);

        assertThat(courseRepository.findById(courseId)).isEmpty();
    }

    @Test
    void shouldReportValidationAndNotFoundErrors() {
        assertSingleGraphQlError(
                graphQlTester
                        .document("""
                            mutation {
                                createStudent(input: { firstName: "", lastName: "Lovelace" }) { id }
                            }
                            """)
                        .execute());

        assertSingleGraphQlError(
                graphQlTester
                        .document("query ($id: ID!) { student(id: $id) { id } }")
                        .variable("id", UUID.randomUUID().toString())
                        .execute());

        assertSingleGraphQlError(
                graphQlTester
                        .document("query ($id: ID!) { student(id: $id) { id } }")
                        .variable("id", "not-a-uuid")
                        .execute());
    }

    private static void assertSingleGraphQlError(GraphQlTester.Response response) {
        response.errors().satisfy(errors -> assertThat(errors).hasSize(1));
    }
}
