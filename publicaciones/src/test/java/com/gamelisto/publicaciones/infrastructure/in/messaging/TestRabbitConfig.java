package com.gamelisto.publicaciones.infrastructure.in.messaging;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestRabbitConfig {

  private static final RabbitMQContainer RABBIT =
      new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.9-management"));

  @DynamicPropertySource
  static void registerRabbitProperties(DynamicPropertyRegistry registry) {
    boolean dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
    if (!dockerAvailable) {
      registry.add("messaging.rabbitmq.enabled", () -> "false");
      return;
    }

    // Start container lazily when Docker is available
    RABBIT.start();

    registry.add("messaging.rabbitmq.enabled", () -> "true");
    registry.add("spring.rabbitmq.host", RABBIT::getHost);
    registry.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
    registry.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
    registry.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
  }

  @Bean
  public RabbitMQContainer rabbitContainer() {
    return RABBIT;
  }
}
