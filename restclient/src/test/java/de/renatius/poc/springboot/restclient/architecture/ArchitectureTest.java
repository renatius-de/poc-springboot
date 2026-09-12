package de.renatius.poc.springboot.restclient.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

@AnalyzeClasses(
    packages = "de.renatius.poc.springboot.restclient",
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  @ArchTest
  static final ArchRule controllersMustNotDependOnRepositories =
      noClasses()
          .that()
          .resideInAPackage("..controller..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..repository..");

  @ArchTest
  static final ArchRule serviceClassesShouldFollowServiceNamingConvention =
      classes().that().resideInAPackage("..service..").should().haveSimpleNameEndingWith("Service");

  @ArchTest
  static final ArchRule repositoryClassesShouldFollowRepositoryNamingConvention =
      classes()
          .that()
          .resideInAPackage("..repository..")
          .should()
          .haveSimpleNameEndingWith("Repository");

  @ArchTest
  static final ArchRule controllerClassesShouldFollowControllerNamingConvention =
      classes()
          .that()
          .resideInAPackage("..controller..")
          .should()
          .haveSimpleNameEndingWith("Controller");

  @ArchTest
  static final ArchRule dtoClassesShouldFollowDtoNamingConvention =
      classes().that().resideInAPackage("..dto..").should().haveSimpleNameEndingWith("Dto");

  @ArchTest
  static final ArchRule domainMustNotDependOnSpringOrWebFrameworks =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..", "jakarta..", "javax..", "..configuration..");

  @ArchTest
  static final ArchRule packagesMustBeCycleFree =
      slices().matching("de.renatius.poc.springboot.restclient.(*)..").should().beFreeOfCycles();
}
