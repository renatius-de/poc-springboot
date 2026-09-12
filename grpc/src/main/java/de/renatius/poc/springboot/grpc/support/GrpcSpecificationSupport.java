package de.renatius.poc.springboot.grpc.support;

import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class GrpcSpecificationSupport {

  private GrpcSpecificationSupport() {}

  public static <T> Specification<T> likeIgnoreCase(String field, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String expression = "%" + value.toLowerCase(Locale.ROOT) + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get(field)), expression);
  }
}
