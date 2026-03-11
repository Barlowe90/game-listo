package com.gamelisto.publicaciones.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

  @Bean
  @ServiceConnection
  public MongoDBContainer mongodbContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
        .withStartupTimeout(Duration.ofMinutes(2));
  }

  @Bean
  @ServiceConnection
  public RabbitMQContainer rabbitMQContainer() {
    return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management-alpine"))
        .withReuse(true);
  }
}
