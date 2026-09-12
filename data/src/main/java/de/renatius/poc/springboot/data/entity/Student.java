package de.renatius.poc.springboot.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "student")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  @ToString.Include
  private UUID id;

  @Column(name = "first_name", nullable = false)
  @ToString.Include
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @ToString.Include
  private String lastName;

  @Builder.Default
  @ManyToMany
  @JoinTable(
      name = "student_course",
      joinColumns = @JoinColumn(name = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "course_id"))
  @Setter(AccessLevel.NONE)
  private Set<Course> courses = new HashSet<>();

  public void addCourse(Course course) {
    if (course == null || !courses.add(course)) {
      return;
    }
    course.getStudents().add(this);
  }

  public void removeCourse(Course course) {
    if (course == null || !courses.remove(course)) {
      return;
    }
    course.getStudents().remove(this);
  }
}
