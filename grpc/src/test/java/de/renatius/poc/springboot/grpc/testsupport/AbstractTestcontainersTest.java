package de.renatius.poc.springboot.grpc.testsupport;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;

import de.renatius.poc.springboot.grpc.GrpcApplication;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

@SpringBootTest(classes = GrpcApplication.class)
@Transactional
public abstract class AbstractTestcontainersTest {

  protected static final PostgreSQLContainer POSTGRESQL =
      new PostgreSQLContainer("postgres:18-alpine");

  static {
    POSTGRESQL.start();
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL::getPassword);
  }

  private final List<BindableService> bindableServices;

  protected ManagedChannel channel;

  @Autowired
  public AbstractTestcontainersTest(List<BindableService> bindableServices) {
    this.bindableServices = bindableServices;
  }

  private Server server;

  @SuppressWarnings("unused")
  @BeforeEach
  void startInProcessServer() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    InProcessServerBuilder builder = InProcessServerBuilder.forName(serverName).directExecutor();
    bindableServices.forEach(builder::addService);
    server = builder.build().start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
  }

  @SuppressWarnings("unused")
  @AfterEach
  void stopInProcessServer() throws InterruptedException {
    if (channel != null) {
      channel.shutdownNow();
      channel.awaitTermination(5, TimeUnit.SECONDS);
    }
    if (server != null) {
      server.shutdownNow();
      server.awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
