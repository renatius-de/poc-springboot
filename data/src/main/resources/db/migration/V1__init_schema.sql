CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE professor
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title      VARCHAR(255),
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE course
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    room         VARCHAR(255),
    professor_id UUID,
    CONSTRAINT fk_course_professor
        FOREIGN KEY (professor_id)
            REFERENCES professor (id)
);

CREATE TABLE student
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE student_course
(
    student_id UUID NOT NULL,
    course_id  UUID NOT NULL,
    PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_student_course_student
        FOREIGN KEY (student_id)
            REFERENCES student (id),
    CONSTRAINT fk_student_course_course
        FOREIGN KEY (course_id)
            REFERENCES course (id)
);

CREATE INDEX idx_course_professor_id ON course (professor_id);
CREATE INDEX idx_student_course_course_id ON student_course (course_id);
