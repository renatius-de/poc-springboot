package de.renatius.poc.springboot.domain.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CourseParticipantTest {
    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        //noinspection resource
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            ",John,Jane,Doe",
            "000000,John,Jane,Doe",
            "00000000,John,Jane,Doe",
            "0000000,,Jane,Doe",
            "0000000,John,Jane,",
    })
    void courseParticipantsWithInvalidInput(final String matriculationNumber,
                                            final String firstname,
                                            final String middleName,
                                            final String lastname) {
        final var courseParticipant = new CourseParticipant(matriculationNumber, firstname, middleName, lastname);
        final var validates = validator.validate(courseParticipant);

        assertThat(validates).hasSize(1);
    }

    @ParameterizedTest
    @CsvSource({
            "0000000,John,Jane,Doe",
            "0000000,John,,Doe",
    })
    void courseParticipantWithValidInput(final String matriculationNumber,
                                         final String firstname,
                                         final String middleName,
                                         final String lastname) {
        final var courseParticipant = new CourseParticipant(matriculationNumber, firstname, middleName, lastname);
        final var validates = validator.validate(courseParticipant);

        assertThat(validates).isEmpty();
    }
}
