package com.gamelist.catalogo_service.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
        .withDatabaseName("catalogo_test_db")
        .withUsername("testuser")
        .withPassword("testpass");
  }

  @Bean
  @ServiceConnection
  MongoDBContainer mongoContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:7.0")).withExposedPorts(27017);
  }
}
