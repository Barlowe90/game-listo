# Guía de Implementación – DDD + Hexagonal

## Estado actual del proyecto

### Fase 1: Dominio (COMPLETADO)

- [x] Value Objects con validación (`UsuarioId`, `Email`, `Username`, `PasswordHash`, `Avatar`)
- [x] Value Objects Discord (`DiscordUserId`, `DiscordUsername`)
- [x] Value Object `TokenVerificacion` para verificación y reset
- [x] Entidad `Usuario` con comportamiento completo
- [x] Enums del dominio (`Rol`, `Idioma`, `EstadoUsuario`)
- [x] Interface `RepositorioUsuarios` (port)
- [x] Excepciones de dominio

### Fase 2: Infraestructura - Persistencia (COMPLETADO)

- [x] `UsuarioEntity` (JPA)
- [x] `UsuarioMapper` (Anti-Corruption Layer)
- [x] `UsuarioJpaRepository` (Spring Data JPA)
- [x] `RepositorioUsuariosPostgre` (implementa port)

### Fase 3: Application - Casos de Uso (COMPLETADO)

- [x] `CrearUsuarioUseCase` - Registro con token de verificación
- [x] `VerificarEmailUseCase` - Verificación con token
- [x] `ReenviarVerificacionUseCase` - Regenerar token expirado
- [x] `SolicitarRestablecimientoUseCase` - Solicitar reset de contraseña
- [x] `RestablecerContrasenaUseCase` - Reset con token
- [x] `CambiarContrasenaUseCase` - Cambio validando actual
- [x] `CambiarCorreoUseCase` - Cambio de email con nueva verificación
- [x] `ObtenerUsuarioPorId` - Consulta por ID
- [x] `ObtenerTodosLosUsuariosUseCase` - Listado
- [x] `EditarPerfilUsuarioUseCase` - Edición de perfil
- [x] `CambiarEstadoUsuarioUseCase` - Suspender/Activar
- [x] `EliminarUsuarioUseCase` - Soft delete
- [x] `VincularDiscordUseCase` - Vincular cuenta Discord
- [x] `DesvincularDiscordUseCase` - Desvincular Discord

### Fase 4: Infrastructure - REST API (COMPLETADO)

- [x] `UsuariosController` con todos los endpoints
- [x] Request/Response DTOs completos
- [x] Validación con Bean Validation
- [x] `GlobalExceptionHandler` para manejo de errores
- [x] `SecurityConfig` con BCrypt
- [x] `DiscordClient` para integración OAuth2

### Fase 5: Testing (COMPLETADO)

- [x] Tests de Value Objects (dominio) - 100% cobertura
- [x] Tests de entidad `Usuario` (dominio)
- [x] Tests de casos de uso con Mockito (application) - todos los casos de uso

### Fase 6: Mensajería (COMPLETADO)

- [x] Interface `IUsuarioPublisher` (port en application)
- [x] `UsuariosPublisher` implementado en infrastructure
- [x] `RabbitMQConfig` con exchange "bus", queue y DLQ
- [x] Eventos de dominio: `UsuarioCreado`, `EmailVerificado`, `UsuarioEliminado`, `UsuarioActiviaNotificaciones`, `UsuarioDesactivaNotificaciones`
- [x] Publicación de eventos integrada en use cases
- [x] `UsuariosListener` para escuchar eventos de otros servicios

### Fase 7: Búsqueda Avanzada (COMPLETADO)

- [x] `BuscarUsuariosPorEstadoUseCase` - Búsqueda por estado
- [x] `BuscarUsuariosConNotificacionesActivadasUseCase` - Usuarios con notificaciones activas
- [x] Métodos en repositorio: `findByStatus()`, `findByStatusAndNotificationsActive()`
- [x] Endpoints REST para búsqueda avanzada

### Fase 8: Pendientes (SIGUIENTES PASOS)

- [ ] **Servicio de Email** - Envío de emails de verificación y reset password
  - [ ] Ver plan completo: `.github/email-implementation-plan.md`
- [ ] **Integración JWT** - Asegurar endpoints con autenticación y autorización
  - [ ] Ver plan completo: `.github/jwt-integration-plan.md`
- [ ] Tests de integración con H2 y SpringBootTest
- [ ] Tests de controladores con MockMvc
- [ ] Completar configuración Discord OAuth2 (variables de entorno)
  - [ ] Ver guía: `.github/discord-oauth2-setup.md`
- [ ] Documentación OpenAPI/Swagger
- [ ] Circuit breaker para llamadas a Discord API
- [ ] Rate limiting en endpoints públicos

## Reglas clave

### Dominio

- Constructor privado + factory methods
- Value Objects inmutables
- Validación en constructores
- Comportamiento, no solo getters
- Sin dependencias externas

### Application

- Un caso de uso = una clase
- Inyecta repositorios por constructor
- Coordina, no ejecuta lógica
- Retorna DTOs de aplicación
- Lanza excepciones de dominio

### Infrastructure

- Adapta dominio a tecnologías
- Mapper traduce entre capas
- Controladores usan Request/Response DTOs
- No expone entities JPA fuera de la capa

## Convenciones de nombres

### Capa Dominio

| Tipo | Ejemplo |
| ---- | -------- |
| Entidades | `Usuario` |
| Value Objects | `UsuarioId`, `Email`, `Username`, `TokenVerificacion` |
| Enums | `EstadoUsuario`, `Rol`, `Idioma` |
| Repositorios | `RepositorioUsuarios` (interface) |
| Eventos | `UsuarioCreado`, `EmailVerificado`, `UsuarioEliminado` |
| Excepciones | `UsuarioNoEncontradoException`, `TokenVerificacionInvalidoException`, `DiscordYaVinculadoException` |

### Capa Application

| Tipo | Ejemplo |
| ---- | -------- |
| Casos de uso | `CrearUsuarioUseCase`, `VerificarEmailUseCase`, `VincularDiscordUseCase` |
| Commands | `CrearUsuarioCommand`, `CambiarContrasenaCommand`, `VincularDiscordCommand` |
| DTOs | `UsuarioDTO` |
| Ports | `IUsuarioPublisher`, `IDiscordService` |

### Capa Infrastructure

| Tipo | Ejemplo |
| ---- | -------- |
| Entities | `UsuarioEntity` |
| Mappers | `UsuarioMapper` |
| Repositorios | `RepositorioUsuariosPostgre`, `UsuarioJpaRepository` |
| Controllers | `UsuariosController` |
| Request DTOs | `CrearUsuarioRequest`, `EditarPerfilUsuarioRequest`, `VincularDiscordRequest` |
| Response DTOs | `UsuarioResponse` |
| Integraciones | `DiscordClient`, `DiscordTokenResponse`, `DiscordUserResponse` |
| Messaging | `UsuariosPublisher`, `UsuariosListener`, `RabbitMQConfig` |

## Testing

| Capa | Tipo | Herramientas |
| ---- | ---- | ------------ |
| Domain | Unit tests puros | JUnit 5, AssertJ |
| Application | Unit tests con mocks | Mockito, JUnit 5 |
| Infrastructure | Integración | SpringBootTest, H2, MockMvc |

## Próximos pasos

1. 📋 Implementar tests de integración REST
2. 📋 Configurar RabbitMQ para eventos
3. 📋 Completar flujo OAuth2 de Discord
4. 📋 Integrar validación JWT desde `auth-service`
5. 📋 Dockerizar el servicio
6. 📋 Configurar CI/CD con GitHub Actions
