package de.renatius.poc.springboot.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "professor")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Professor {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  @ToString.Include
  private UUID id;

  @Column(name = "title")
  private String title;

  @Column(name = "first_name", nullable = false)
  @ToString.Include
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @ToString.Include
  private String lastName;

  @Builder.Default
  @OneToMany(mappedBy = "professor")
  @Setter(AccessLevel.NONE)
  @ToString.Exclude
  private List<Course> courses = new ArrayList<>();

  public void addCourse(Course course) {
    if (course == null) {
      return;
    }
    course.setProfessor(this);
  }

  public void removeCourse(Course course) {
    if (course == null) {
      return;
    }
    courses.remove(course);
    if (course.getProfessor() == this) {
      course.setProfessor(null);
    }
  }
}
