package com.gamelisto.usuarios_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación automática de la API REST.
 * 
 * Acceso a la documentación:
 * - Swagger UI: http://localhost:8081/swagger-ui/index.html
 * - OpenAPI JSON: http://localhost:8081/v3/api-docs
 * - OpenAPI YAML: http://localhost:8081/v3/api-docs.yaml
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:usuarios-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameListo - API de Usuarios")
                        .version("1.0.0")
                        .description("""
                                API REST del microservicio de gestión de usuarios de la plataforma GameListo.
                                
                                **Funcionalidades principales:**
                                - 🔐 Registro y autenticación de usuarios
                                - 👤 Gestión de perfiles (username, email, avatar, idioma)
                                - ✉️ Verificación de email con tokens de 24h
                                - 🔑 Recuperación de contraseñas
                                - 🎮 Integración con Discord OAuth2
                                - 🔔 Configuración de notificaciones
                                - 👥 Estados de usuario (PENDIENTE, ACTIVO, SUSPENDIDO, ELIMINADO)
                                - 🛡️ Roles (USER, ADMIN, MODERATOR)
                                
                                **Arquitectura:**
                                - Hexagonal Architecture (Ports & Adapters)
                                - Domain-Driven Design (DDD)
                                - Spring Boot 3.5.8 + Java 21
                                - PostgreSQL (producción) / H2 (desarrollo)
                                """)
                        .contact(new Contact()
                                .name("GameListo Team")
                                .email("soporte@gamelisto.com")
                                .url("https://github.com/game-listo"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.gamelisto.com/usuarios")
                                .description("Servidor de producción")
                ));
    }
}
