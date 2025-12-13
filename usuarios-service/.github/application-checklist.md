# Application Checklist – Casos de Uso

## ✅ Estructura de casos de uso

- [x] Cada caso de uso en `/application/usecases/`.
- [x] Anotados con `@Service`.
- [x] Inyección de `RepositorioUsuarios` por constructor.
- [x] Sin acceso directo a JPA repositories.

## ✅ Casos de uso implementados

### Registro y Verificación

- [x] `CrearUsuarioUseCase` - POST /auth/register
- [x] `VerificarEmailUseCase` - POST /auth/verify-email
- [x] `ReenviarVerificacionUseCase` - POST /auth/resend-verification

### Gestión de Contraseñas

- [x] `CambiarContrasenaUseCase` - POST /user/{id}/change-password
- [x] `SolicitarRestablecimientoUseCase` - POST /auth/forgot-password
- [x] `RestablecerContrasenaUseCase` - POST /auth/reset-password

### Gestión de Perfil

- [x] `ObtenerUsuarioPorId` - GET /user/{id}
- [x] `ObtenerTodosLosUsuariosUseCase` - GET /users
- [x] `EditarPerfilUsuarioUseCase` - PATCH /user/{id}
- [x] `CambiarCorreoUseCase` - POST /user/{id}/change-email

### Gestión de Estado

- [x] `CambiarEstadoUsuarioUseCase` - PATCH /user/{id}/state
- [x] `EliminarUsuarioUseCase` - DELETE /user/{id}

### Discord

- [x] `VincularDiscordUseCase` - POST /auth/discord/link/callback
- [x] `DesvincularDiscordUseCase` - DELETE /discord/link

## ✅ DTOs de aplicación

- [x] Commands: `CrearUsuarioCommand`, `EditarPerfilUsuarioCommand`, `CambiarContrasenaCommand`, `CambiarEstadoUsuarioCommand`, `CambiarCorreoCommand`, `VerificarEmailCommand`, `RestablecerContrasenaCommand`, `SolicitarRestablecimientoCommand`, `VincularDiscordCommand`, `ReenviarVerificacionCommand`
- [x] DTOs de salida: `UsuarioDTO`
- [x] Conversión Domain → DTO en el caso de uso

## 📋 Publicación de eventos (pendiente)

- [ ] Interface `IEventosPublisher` en `/application/ports/`
- [ ] Implementación en `/infrastructure/messaging/`
- [ ] Eventos: `UsuarioCreado`, `EmailVerificado`, `ContrasenaRestablecida`

## ✅ Reglas implementadas

- [x] Casos de uso coordinan flujo, no ejecutan lógica de negocio
- [x] Lógica de negocio en entidad de dominio (`Usuario`)
- [x] No conocen detalles de API REST
- [x] Retornan `UsuarioDTO`
- [x] Lanzan excepciones de dominio, no HTTP

## ⛔ Prohibiciones en /application

- [x] No usar entities JPA (`UsuarioEntity`)
- [x] No usar anotaciones REST (`@RestController`, `@GetMapping`)
- [x] No manejar HTTP status codes directamente
- [x] No lanzar excepciones HTTP (usar excepciones de dominio)
- [x] No acceder directamente a bases de datos

## ℹ️ Permitido en /application

- [x] Logger (SLF4J) para trazabilidad de casos de uso
- [x] Anotaciones Spring (`@Service`, `@Transactional`)
- [x] Inyección de dependencias por constructor
