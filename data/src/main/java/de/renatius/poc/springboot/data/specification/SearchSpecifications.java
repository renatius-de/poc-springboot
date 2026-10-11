package de.renatius.poc.springboot.data.specification;

import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class SearchSpecifications {

  private SearchSpecifications() {}

  public static <T> Specification<T> likeIgnoreCase(String field, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String escapedValue =
        value
            .toLowerCase(Locale.ROOT)
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    String pattern = "%" + escapedValue + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get(field)), pattern, '\\');
  }
}
