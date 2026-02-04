package com.gamelisto.usuarios_service.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación interactiva de la API.
 *
 * <p>URLs de acceso:
 *
 * <ul>
 *   <li>Swagger UI: http://localhost:8081/swagger-ui/index.html
 *   <li>OpenAPI JSON: http://localhost:8081/v3/api-docs
 *   <li>OpenAPI YAML: http://localhost:8081/v3/api-docs.yaml
 * </ul>
 *
 * <p><b>Autenticación:</b> Esta API utiliza JWT Bearer tokens. Para probar endpoints protegidos:
 *
 * <ol>
 *   <li>Hacer login en POST /v1/usuarios/auth/login
 *   <li>Copiar el accessToken de la respuesta
 *   <li>Hacer clic en "Authorize" en Swagger UI
 *   <li>Pegar el token en el campo "Value" (sin "Bearer ")
 *   <li>Los endpoints protegidos incluirán automáticamente el header: Authorization: Bearer {token}
 * </ol>
 */
@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name:usuarios-service}")
  private String applicationName;

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
        .info(
            new Info()
                .title("GameListo - API de Usuarios")
                .version("1.0.0")
                .description(
                    """
                                                                API REST del microservicio de gestión de usuarios de la plataforma GameListo.

                                                                **Funcionalidades principales:**
                                                                - Registro y autenticación de usuarios
                                                                - Gestión de perfiles (username, email, avatar, idioma)
                                                                - Verificación de email con tokens de 24h
                                                                - Recuperación de contraseñas
                                                                - Configuración de notificaciones
                                                                - Estados de usuario (PENDIENTE, ACTIVO, SUSPENDIDO, ELIMINADO)
                                                                - Roles (USER, ADMIN, MODERATOR)

                                                                **Arquitectura:**
                                                                - Hexagonal Architecture (Ports & Adapters)
                                                                - Domain-Driven Design (DDD)
                                                                - Spring Boot 3.5.8 + Java 21
                                                                - PostgreSQL (producción) / H2 (desarrollo)

                                                                **Seguridad:**
                                                                - Autenticación mediante JWT (JSON Web Tokens)
                                                                - Access Token: 15 minutos de validez
                                                                - Refresh Token: 7 días de validez con rotación automática
                                                                - Control de acceso basado en roles (RBAC)
                                                                - Validación de tokens delegada al API Gateway
                                                                """)
                .contact(
                    new Contact()
                        .name("GameListo Team")
                        .email("soporte@gamelisto.com")
                        .url("https://github.com/game-listo"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server()
                    .url("http://localhost:8081")
                    .description("Servidor de desarrollo local")))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description(
                            "JWT Bearer token obtenido del endpoint /v1/usuarios/auth/login. "
                                + "El token se incluye automáticamente en el header: Authorization: Bearer {token}")))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
  }
}
