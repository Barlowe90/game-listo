package com.gamelisto.usuarios_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuración de Testcontainers para tests de integración. Proporciona un contenedor PostgreSQL
 * compartido para todos los tests, garantizando paridad con el entorno de producción.
 *
 * <p>Características:
 *
 * <ul>
 *   <li>PostgreSQL 16 (misma versión que producción)
 *   <li>Reutilización de contenedor entre tests para mayor velocidad
 *   <li>Configuración automática del DataSource via @ServiceConnection
 * </ul>
 *
 * @author GameListo Team
 * @since 1.0
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

  /**
   * Contenedor PostgreSQL compartido entre tests.
   *
   * <p>@ServiceConnection configura automáticamente spring.datasource.* eliminando la necesidad de
   * configuración manual en application-test.properties.
   *
   * @return Contenedor PostgreSQL 16 configurado y listo para usar
   */
  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
        .withDatabaseName("usuarios_test_db")
        .withUsername("test_user")
        .withPassword("test_password")
        .withReuse(true); // Reutiliza el contenedor entre ejecuciones para mayor velocidad
  }
}
