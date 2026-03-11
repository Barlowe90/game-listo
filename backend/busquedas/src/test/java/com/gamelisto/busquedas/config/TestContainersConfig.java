package com.gamelisto.busquedas.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

  @SuppressWarnings("resource")
  @Bean
  @ServiceConnection
  RabbitMQContainer rabbitMQContainer() {
    return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management-alpine"))
        .withReuse(true);
  }
}
