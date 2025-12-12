# Guía de Implementación – DDD + Hexagonal

## 🎯 Estado actual del proyecto

### Fase 1: Dominio (✅ COMPLETADO)

- [x] Value Objects con validación (`UsuarioId`, `Email`, `Username`, `PasswordHash`, `Avatar`)
- [x] Value Objects Discord (`DiscordUserId`, `DiscordUsername`)
- [x] Value Object `TokenVerificacion` para verificación y reset
- [x] Entidad `Usuario` con comportamiento completo
- [x] Enums del dominio (`Rol`, `Idioma`, `EstadoUsuario`)
- [x] Interface `RepositorioUsuarios` (port)
- [x] Excepciones de dominio

### Fase 2: Infraestructura - Persistencia (✅ COMPLETADO)

- [x] `UsuarioEntity` (JPA)
- [x] `UsuarioMapper` (Anti-Corruption Layer)
- [x] `UsuarioJpaRepository` (Spring Data JPA)
- [x] `RepositorioUsuariosPostgre` (implementa port)

### Fase 3: Application - Casos de Uso (✅ COMPLETADO)

- [x] `CrearUsuarioUseCase` - Registro con token de verificación
- [x] `VerificarEmailUseCase` - Verificación con token
- [x] `ReenviarVerificacionUseCase` - Regenerar token expirado
- [x] `RestablecerContrasenaUseCase` - Reset con token
- [x] `CambiarContrasenaUseCase` - Cambio validando actual
- [x] `ObtenerUsuarioPorId` - Consulta por ID
- [x] `ObtenerTodosLosUsuariosUseCase` - Listado
- [x] `EditarPerfilUsuarioUseCase` - Edición de perfil
- [x] `CambiarEstadoUsuarioUseCase` - Suspender/Activar
- [x] `EliminarUsuarioUseCase` - Soft delete

### Fase 4: Infrastructure - REST API (✅ COMPLETADO)

- [x] `UsuariosController` con todos los endpoints
- [x] Request/Response DTOs
- [x] Validación con Bean Validation
- [x] `GlobalExceptionHandler` para manejo de errores
- [x] `SecurityConfig` con BCrypt

### Fase 5: Testing (✅ COMPLETADO)

- [x] Tests de Value Objects (dominio)
- [x] Tests de entidad `Usuario` (dominio)
- [x] Tests de casos de uso con Mockito (application)

### Fase 6: Pendientes (📋 EN PROGRESO)

- [ ] Tests de integración con H2
- [ ] Tests de controladores con MockMvc
- [ ] Integración JWT con `auth-service`
- [ ] Mensajería RabbitMQ (eventos)
- [ ] Discord OAuth2 completo

## 🔑 Reglas clave

### ✅ Dominio

- Constructor privado + factory methods
- Value Objects inmutables
- Validación en constructores
- Comportamiento, no solo getters
- Sin dependencias externas

### ✅ Application

- Un caso de uso = una clase
- Inyecta repositorios por constructor
- Coordina, no ejecuta lógica
- Retorna DTOs de aplicación
- Lanza excepciones de dominio

### ✅ Infrastructure

- Adapta dominio a tecnologías
- Mapper traduce entre capas
- Controladores usan Request/Response DTOs
- No expone entities JPA fuera de la capa

## 📝 Convenciones de nombres

### Dominio

| Tipo | Ejemplo |
|------|----------|
| Entidades | `Usuario` |
| Value Objects | `UsuarioId`, `Email`, `Username`, `TokenVerificacion` |
| Enums | `EstadoUsuario`, `Rol`, `Idioma` |
| Repositorios | `RepositorioUsuarios` (interface) |
| Excepciones | `EntidadNoEncontrada`, `TokenInvalidoException` |

### Application

| Tipo | Ejemplo |
|------|----------|
| Casos de uso | `CrearUsuarioUseCase`, `VerificarEmailUseCase` |
| Commands | `CrearUsuarioCommand`, `CambiarContrasenaCommand` |
| DTOs | `UsuarioDTO` |

### Infrastructure

| Tipo | Ejemplo |
|------|----------|
| Entities | `UsuarioEntity` |
| Mappers | `UsuarioMapper` |
| Repositorios | `RepositorioUsuariosPostgre`, `UsuarioJpaRepository` |
| Controllers | `UsuariosController` |
| Request DTOs | `CrearUsuarioRequest`, `EditarPerfilUsuarioRequest` |
| Response DTOs | `UsuarioResponse` |

## 🧪 Testing

| Capa | Tipo | Herramientas |
|------|------|-------------|
| Domain | Unit tests puros | JUnit 5, AssertJ |
| Application | Unit tests con mocks | Mockito, JUnit 5 |
| Infrastructure | Integración | SpringBootTest, H2, MockMvc |

## 🚀 Próximos pasos

1. 📋 Implementar tests de integración REST
2. 📋 Configurar RabbitMQ para eventos
3. 📋 Completar flujo OAuth2 de Discord
4. 📋 Integrar validación JWT desde `auth-service`
5. 📋 Dockerizar el servicio
6. 📋 Configurar CI/CD con GitHub Actions
