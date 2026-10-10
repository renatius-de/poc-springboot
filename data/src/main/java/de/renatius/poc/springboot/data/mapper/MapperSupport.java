package de.renatius.poc.springboot.data.mapper;

public final class MapperSupport {

  private MapperSupport() {}

  public static <T> T requireSource(T source, String name) {
    if (source == null) {
      throw new IllegalArgumentException("Mapping source '" + name + "' must not be null");
    }
    return source;
  }
}
