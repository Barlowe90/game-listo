# Infrastructure Checklist – Microservicio Usuarios

## Persistencia completada

- [x] `UsuarioEntity` en `/infrastructure/persistence/postgres/entity/`
- [x] Entity como POJO puro con anotaciones JPA
- [x] Sin lógica de negocio en UsuarioEntity
- [x] Importa enums del dominio (`Rol`, `Idioma`, `EstadoUsuario`)
- [x] Campos de verificación: `tokenVerificacion`, `tokenVerificacionExpiracion`
- [x] Campos de Discord: `discordUserId`, `discordUsername`, `discordLinkedAt`, `discordConsent`

## Mapper (Anti-Corruption Layer)

- [x] `UsuarioMapper` en `/infrastructure/persistence/postgres/mapper/`
- [x] Anotado con `@Component`
- [x] `toEntity()`: Usuario → UsuarioEntity
- [x] `toDomain()`: UsuarioEntity → Usuario (usa `reconstitute()`)
- [x] Extrae valores de VOs y reconstruye correctamente
- [x] Maneja campos opcionales (avatar, discord, token)

## Repositorio JPA implementado

- [x] Interface `UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID>`
- [x] Implementación `RepositorioUsuariosPostgre implements RepositorioUsuarios`
- [x] Usa `UsuarioMapper` para conversión
- [x] Query methods: `findByEmail()`, `findByUsername()`, `existsByUsername()`, `existsByEmail()`
- [x] Query method: `findByTokenVerificacion()` para verificación de email

## Controladores REST implementados

- [x] Base path: `/v1/usuarios`
- [x] `UsuariosController` con todos los endpoints
- [x] Request DTOs en `/infrastructure/api/dto/`: `CrearUsuarioRequest`, `EditarPerfilUsuarioRequest`, `CambiarContrasenaRequest`, `CambiarCorreoRequest`, `CambiarEstadoUsuarioRequest`, `VerificarEmailRequest`, `RestablecerContrasenaRequest`, `SolicitarRestablecimientoRequest`, `VincularDiscordRequest`, `ReenviarVerificacionRequest`
- [x] Response DTOs: `UsuarioResponse`
- [x] Validación con `@Valid` en requests
- [x] Inyecta casos de uso, no repositorios

### Endpoints implementados

| Método | Endpoint | Use Case |
| ------ | -------- | -------- |
| GET | `/health` | Health check |
| POST | `/auth/register` | `CrearUsuarioUseCase` |
| POST | `/auth/verify-email` | `VerificarEmailUseCase` |
| POST | `/auth/resend-verification` | `ReenviarVerificacionUseCase` |
| POST | `/auth/forgot-password` | `SolicitarRestablecimientoUseCase` |
| POST | `/auth/reset-password` | `RestablecerContrasenaUseCase` |
| GET | `/users` | `ObtenerTodosLosUsuariosUseCase` |
| GET | `/users?estado={estado}` | `BuscarUsuariosPorEstadoUseCase` |
| GET | `/users/notifications-enabled` | `BuscarUsuariosConNotificacionesActivadasUseCase` |
| GET | `/user/{id}` | `ObtenerUsuarioPorId` |
| PATCH | `/user/{id}` | `EditarPerfilUsuarioUseCase` |
| DELETE | `/user/{id}` | `EliminarUsuarioUseCase` |
| POST | `/user/{id}/change-password` | `CambiarContrasenaUseCase` |
| POST | `/user/{id}/change-email` | `CambiarCorreoUseCase` |
| PATCH | `/user/{id}/state` | `CambiarEstadoUsuarioUseCase` |
| POST | `/auth/discord/link/callback` | `VincularDiscordUseCase` |
| DELETE | `/discord/link` | `DesvincularDiscordUseCase` |

## Seguridad JWT (pendiente)

- [ ] Dependencias JWT (jjwt) agregadas en `pom.xml`
- [ ] `JwtConfig.java` - Configuración JWT (secret, expiration, issuer)
- [ ] `JwtUtil.java` - Utilidades para validar y extraer claims
- [ ] `JwtAuthenticationFilter.java` - Filtro para validar JWT en requests
- [ ] `CustomAuthenticationEntryPoint.java` - Manejo de errores 401/403
- [ ] `OwnershipValidator.java` - Validación de ownership de recursos
- [ ] `SecurityConfig` actualizado con reglas de autorización:
  - [ ] Endpoints públicos configurados (register, verify-email, etc.)
  - [ ] Endpoints protegidos USER (requieren autenticación + ownership)
  - [ ] Endpoints protegidos ADMIN (solo administradores)
- [ ] Validación de ownership en controladores
- [ ] Configuración JWT en `application.properties`
- [ ] Variables de entorno en `.env.example`
- [ ] Coordinación con `auth-service` para `jwt.secret` compartida
- [ ] Testing con tokens válidos e inválidos
- [ ] **Guía completa**: `.github/jwt-integration-plan.md`

## Discord OAuth2 (completamente implementado)

- [x] `DiscordClient` para integración con API de Discord
- [x] `DiscordServiceAdapter` implementando `IDiscordService` port
- [x] `DiscordTokenResponse`, `DiscordUserResponse` DTOs
- [x] `VincularDiscordUseCase`, `DesvincularDiscordUseCase` implementados
- [x] Endpoints de vinculación/desvinculación
- [x] Configuración completa en `application.properties`:
  - [x] URLs de OAuth2 (authorization, token, user-info)
  - [x] Redirect URIs para desarrollo y producción
  - [x] Scopes configurados (identify)
- [x] Variables de entorno configuradas (CLIENT_ID, CLIENT_SECRET, REDIRECT_URIs)
- [x] Guía de configuración paso a paso en `.github/discord-oauth2-setup.md`
- [x] Archivo `.env.example` creado como plantilla
- [ ] Crear aplicación en Discord Developer Portal (paso manual del usuario)
- [ ] Configurar redirect URIs en Discord Portal (paso manual del usuario)

## Manejo de errores implementado

- [x] `@RestControllerAdvice` para excepciones (`GlobalExceptionHandler`)
- [x] Mapea excepciones de dominio a HTTP status
- [x] Respuestas de error estandarizadas

## Mensajería implementada

- [x] `UsuariosPublisher` en `/infrastructure/messaging/publishers/`
- [x] `UsuariosListener` en `/infrastructure/messaging/listeners/`
- [x] `RabbitMQConfig` en `/infrastructure/messaging/config/`
- [x] Exchange: "bus" (TopicExchange)
- [x] Queue: "usuarios.queue" con DLQ configurada
- [x] Routing key prefix: "bus.usuarios"
- [x] Binding pattern: "bus.*.#" para escuchar eventos de otros servicios
- [x] Converter JSON configurado con Jackson (soporte JavaTimeModule)
- [x] Eventos publicados: `UsuarioCreado`, `EmailVerificado`, `UsuarioEliminado`, `UsuarioActiviaNotificaciones`, `UsuarioDesactivaNotificaciones`
