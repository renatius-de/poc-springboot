create table courses
(
    uid   uuid default gen_random_uuid() not null
        constraint pk__courses
            primary key,
    title varchar(1023)                  not null
);

create table professors
(
    uid         uuid default gen_random_uuid() not null
        constraint pk__professors
            primary key,
    firstname   varchar(1023)                  not null,
    middle_name varchar(1023),
    surname     varchar(1023)                  not null
);

create table students
(
    uid                  uuid default gen_random_uuid() not null
        constraint pk__students
            primary key,
    firstname            varchar(1023)                  not null,
    middle_name          varchar(1023),
    lastname             varchar(1023)                  not null,
    matriculation_number varchar(7)                     not null
        constraint udx__students__matriculation_number
            unique
);

create table professors_holds_courses
(
    course_uid    uuid not null
        constraint fk__professors_holds_courses__courses
            references courses
            on update cascade on delete cascade,
    professor_uid uuid not null
        constraint fk__professors_holds_courses__professors
            references professors
            on update cascade on delete cascade,
    constraint pk__professors_holds_courses
        primary key (course_uid, professor_uid)
);

create table students_takes_part_in_courses
(
    course_uid  uuid not null
        constraint fk__students_take_part_in_courses__courses
            references courses
            on update cascade on delete cascade,
    student_uid uuid not null
        constraint fk__students_take_part_in_courses__students
            references students
            on update cascade on delete cascade,
    constraint pk__students_take_part_in_courses
        primary key (course_uid, student_uid)
);
