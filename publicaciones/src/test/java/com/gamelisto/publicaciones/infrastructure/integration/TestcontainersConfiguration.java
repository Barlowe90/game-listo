package com.gamelisto.publicaciones.infrastructure.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

/**
 * Configuración compartida de Testcontainers para tests de integración.
 *
 * <p>Registra MongoDB con @ServiceConnection, por lo que Spring Boot autoconfigura automáticamente
 * spring.data.mongodb.uri.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  MongoDBContainer mongoContainer() {
    return new MongoDBContainer("mongo:7.0");
  }
}
