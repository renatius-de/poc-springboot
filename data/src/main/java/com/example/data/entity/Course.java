package com.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "course")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  @ToString.Include
  private UUID id;

  @Column(name = "name", nullable = false)
  @ToString.Include
  private String name;

  @Column(name = "room")
  private String room;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "professor_id")
  @ToString.Exclude
  private Professor professor;

  @Builder.Default
  @ManyToMany(mappedBy = "courses")
  @ToString.Exclude
  private Set<Student> students = new HashSet<>();
}
