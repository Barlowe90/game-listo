package com.gamelisto.usuarios.test.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainerExtension implements BeforeAllCallback {

  private static GenericContainer<?> redisContainer;

  @Override
  public void beforeAll(ExtensionContext context) {
    if (redisContainer == null) {
      // Start a lightweight Redis container for tests
      redisContainer =
          new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
      try {
        redisContainer.start();
      } catch (RuntimeException e) {
        // If Docker/Testcontainers is not available, rethrow to fail fast in CI
        throw e;
      }

      String host = redisContainer.getHost();
      Integer port = redisContainer.getMappedPort(6379);

      // Set system properties so Spring Boot test environment picks them up
      System.setProperty("spring.data.redis.host", host);
      System.setProperty("spring.data.redis.port", String.valueOf(port));
    }
  }
}
