package com.gamelisto.publicaciones;

import java.time.Duration;

import com.gamelisto.publicaciones.config.TestMessagingConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base para tests de integración que necesitan MongoDB.
 *
 * <p>Usa Testcontainers para levantar contenedores Docker automáticamente durante la ejecución de
 * los tests.
 *
 * <p>Los contenedores son compartidos entre todos los tests de la misma clase para mejorar el
 * rendimiento.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import(TestMessagingConfig.class)
public abstract class AbstractIntegrationTest {

  // Sin @ServiceConnection: la URI se configura manualmente con la BD incluida
  @Container
  static final MongoDBContainer mongodb =
      new MongoDBContainer("mongo:8.0")
          .withStartupTimeout(Duration.ofMinutes(2))
          .withStartupAttempts(3);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", () -> mongodb.getConnectionString() + "/catalogo_test");
  }
}
