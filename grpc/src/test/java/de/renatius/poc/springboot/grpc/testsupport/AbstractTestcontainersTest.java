package de.renatius.poc.springboot.grpc.testsupport;

import de.renatius.poc.springboot.grpc.GrpcApplication;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = GrpcApplication.class)
@Transactional
public abstract class AbstractTestcontainersTest {

  @Container
  @ServiceConnection
  protected static final PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>("postgres:17-alpine");

  @Autowired private List<BindableService> bindableServices;

  protected ManagedChannel channel;

  private Server server;

  @BeforeEach
  void startInProcessServer() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    InProcessServerBuilder builder = InProcessServerBuilder.forName(serverName).directExecutor();
    bindableServices.forEach(builder::addService);
    server = builder.build().start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
  }

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
