package de.renatius.poc.springboot.data.mongodb.document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "student")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocument {

  @Id
  private UUID id;

  private String firstName;
  private String lastName;

  @Builder.Default
  private Set<UUID> courseIds = new HashSet<>();
}
