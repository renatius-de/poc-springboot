package de.renatius.poc.springboot.data.mongodb.document;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "course")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDocument {

  @Id
  private UUID id;

  private String name;
  private String room;
  private UUID professorId;
}
