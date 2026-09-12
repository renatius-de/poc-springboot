package de.renatius.poc.springboot.grpc.support;

import de.renatius.poc.springboot.grpc.v1.SearchPageRequest;
import de.renatius.poc.springboot.grpc.v1.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class GrpcPagingSupport {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;

  private GrpcPagingSupport() {}

  public static Pageable toPageable(SearchPageRequest pageRequest, String... defaultSortProperties) {
    if (pageRequest == null) {
      return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by(Sort.Direction.ASC, defaultSortProperties));
    }

    int page = Math.max(pageRequest.getPage(), DEFAULT_PAGE);
    int size = pageRequest.getSize() > 0 ? pageRequest.getSize() : DEFAULT_SIZE;
    Sort.Direction direction =
        pageRequest.getSortDirection() == SortDirection.SORT_DIRECTION_DESC
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

    String sortBy = pageRequest.getSortBy();
    if (sortBy == null || sortBy.isBlank()) {
      return PageRequest.of(page, size, Sort.by(direction, defaultSortProperties));
    }

    return PageRequest.of(page, size, Sort.by(direction, sortBy));
  }
}
