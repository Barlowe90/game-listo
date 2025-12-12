# Infrastructure Checklist – Microservicio Usuarios

## ✅ Persistencia completada

- [x] `UsuarioEntity` en `/infrastructure/persistence/postgres/entity/`
- [x] Entity como POJO puro con anotaciones JPA
- [x] Sin lógica de negocio en UsuarioEntity
- [x] Importa enums del dominio (`Rol`, `Idioma`, `EstadoUsuario`)
- [x] Campos de verificación: `tokenVerificacion`, `tokenVerificacionExpiracion`
- [x] Campos de Discord: `discordUserId`, `discordUsername`, `discordLinkedAt`, `discordConsent`

## ✅ Mapper (Anti-Corruption Layer)

- [x] `UsuarioMapper` en `/infrastructure/persistence/postgres/mapper/`
- [x] Anotado con `@Component`
- [x] `toEntity()`: Usuario → UsuarioEntity
- [x] `toDomain()`: UsuarioEntity → Usuario (usa `reconstitute()`)
- [x] Extrae valores de VOs y reconstruye correctamente
- [x] Maneja campos opcionales (avatar, discord, token)

## ✅ Repositorio JPA implementado

- [x] Interface `UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID>`
- [x] Implementación `RepositorioUsuariosPostgre implements RepositorioUsuarios`
- [x] Usa `UsuarioMapper` para conversión
- [x] Query methods: `findByEmail()`, `findByUsername()`, `existsByUsername()`, `existsByEmail()`
- [x] Query method: `findByTokenVerificacion()` para verificación de email

## ✅ Controladores REST implementados

- [x] Base path: `/v1/usuarios`
- [x] `UsuariosController` con todos los endpoints
- [x] Request DTOs en `/infrastructure/api/dto/`: `CrearUsuarioRequest`, `EditarPerfilUsuarioRequest`, etc.
- [x] Response DTOs: `UsuarioResponse`
- [x] Validación con `@Valid` en requests
- [x] Inyecta casos de uso, no repositorios

### Endpoints implementados

| Método | Endpoint | Use Case |
|--------|----------|----------|
| GET | `/health` | Health check |
| POST | `/auth/register` | `CrearUsuarioUseCase` |
| POST | `/auth/verify-email` | `VerificarEmailUseCase` |
| POST | `/auth/resend-verification` | `ReenviarVerificacionUseCase` |
| POST | `/auth/reset-password` | `RestablecerContrasenaUseCase` |
| GET | `/users` | `ObtenerTodosLosUsuariosUseCase` |
| GET | `/user/{id}` | `ObtenerUsuarioPorId` |
| PATCH | `/user/{id}` | `EditarPerfilUsuarioUseCase` |
| DELETE | `/user/{id}` | `EliminarUsuarioUseCase` |
| POST | `/user/{id}/change-password` | `CambiarContrasenaUseCase` |
| PATCH | `/user/{id}/state` | `CambiarEstadoUsuarioUseCase` |

## ✅ Seguridad configurada

- [x] `SecurityConfig` con BCryptPasswordEncoder (strength: 10)
- [x] Configuración permite todas las requests (modo desarrollo)
- [ ] Integración JWT pendiente (delegar a `auth-service`)
- [ ] `@PreAuthorize` donde corresponda

## ✅ Manejo de errores implementado

- [x] `@RestControllerAdvice` para excepciones (`GlobalExceptionHandler`)
- [x] Mapea excepciones de dominio a HTTP status
- [x] Respuestas de error estandarizadas

## 📋 Mensajería (pendiente)

- [ ] Publishers en `/infrastructure/messaging/publishers/`
- [ ] Listeners en `/infrastructure/messaging/listeners/`
- [ ] Config RabbitMQ en `/infrastructure/messaging/config/`
- [ ] Eventos: `UsuarioCreado`, `EmailVerificado`, `ContrasenaRestablecida`
