package de.renatius.poc.springboot.grpc.support;

import de.renatius.poc.springboot.data.specification.SearchSpecifications;
import org.springframework.data.jpa.domain.Specification;

public final class GrpcSpecificationSupport {

  private GrpcSpecificationSupport() {}

  public static <T> Specification<T> likeIgnoreCase(String field, String value) {
    return SearchSpecifications.likeIgnoreCase(field, value);
  }
}
