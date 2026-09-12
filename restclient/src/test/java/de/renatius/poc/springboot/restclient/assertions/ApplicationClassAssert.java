package de.renatius.poc.springboot.restclient.assertions;

import java.lang.reflect.Modifier;
import org.assertj.core.api.AbstractAssert;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ApplicationClassAssert extends AbstractAssert<ApplicationClassAssert, Class<?>> {

  public ApplicationClassAssert(Class<?> actual) {
    super(actual, ApplicationClassAssert.class);
  }

  public static ApplicationClassAssert assertThat(Class<?> actual) {
    return new ApplicationClassAssert(actual);
  }

  public ApplicationClassAssert isSpringBootApplication() {
    isNotNull();
    if (!actual.isAnnotationPresent(SpringBootApplication.class)) {
      failWithMessage(
          "Expected class <%s> to be annotated with @SpringBootApplication",
          actual.getName());
    }
    return this;
  }

  public ApplicationClassAssert hasStaticMainMethod() {
    isNotNull();
    try {
      var mainMethod = actual.getDeclaredMethod("main", String[].class);
      var modifiers = mainMethod.getModifiers();
      if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
        failWithMessage(
            "Expected class <%s> to define a public static main(String[]) method",
            actual.getName());
      }
      if (!Void.TYPE.equals(mainMethod.getReturnType())) {
        failWithMessage("Expected class <%s> main(String[]) to return void", actual.getName());
      }
    } catch (NoSuchMethodException e) {
      failWithMessage("Expected class <%s> to define a main(String[]) method", actual.getName());
    }
    return this;
  }
}
