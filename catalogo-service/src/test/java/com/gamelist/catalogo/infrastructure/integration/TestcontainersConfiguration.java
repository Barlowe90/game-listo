package com.gamelist.catalogo_service.infrastructure.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Configuración compartida de Testcontainers para tests de integración.
 *
 * <p>Registra los containers de PostgreSQL y MongoDB con @ServiceConnection, por lo que Spring Boot
 * autoconfigura automáticamente los datasources sin necesidad de propiedades manuales.
 *
 * <p>RabbitMQ se excluye de la autoconfiguración en application-test.properties, por lo que no
 * necesita container aquí. Los tests de EventPublisher que lo necesiten pueden extender esto.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("catalogo_test")
        .withUsername("test")
        .withPassword("test");
  }

  @Bean
  @ServiceConnection
  MongoDBContainer mongoContainer() {
    return new MongoDBContainer("mongo:7.0");
  }
}
