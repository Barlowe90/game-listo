# Architecture Checklist – Hexagonal + DDD

## ✅ Estructura de carpetas completada

```text
/domain
  /usuario
    Usuario.java (Aggregate Root)
    UsuarioId.java, Email.java, Username.java (Value Objects)
    PasswordHash.java, Avatar.java (Value Objects)
    DiscordUserId.java, DiscordUsername.java (Value Objects)
    TokenVerificacion.java (Value Object)
    Rol.java, Idioma.java, EstadoUsuario.java (Enums)
  /repositories
    RepositorioUsuarios.java (Port)
  /exceptions
    EntidadNoEncontrada.java
    UsernameYaExisteException.java
    EmailYaRegistradoException.java
    TokenInvalidoException.java

/application
  /dto
    UsuarioDTO.java
    CrearUsuarioCommand.java
    EditarPerfilUsuarioCommand.java
    CambiarContrasenaCommand.java
    CambiarEstadoUsuarioCommand.java
    VerificarEmailCommand.java
    RestablecerContrasenaCommand.java
  /usecases
    CrearUsuarioUseCase.java
    ObtenerUsuarioPorId.java
    ObtenerTodosLosUsuariosUseCase.java
    EditarPerfilUsuarioUseCase.java
    EliminarUsuarioUseCase.java
    CambiarEstadoUsuarioUseCase.java
    CambiarContrasenaUseCase.java
    VerificarEmailUseCase.java
    ReenviarVerificacionUseCase.java
    RestablecerContrasenaUseCase.java

/infrastructure
  /persistence/postgres
    /entity
      UsuarioEntity.java (Adapter JPA)
    /mapper
      UsuarioMapper.java (Anti-Corruption Layer)
    /repository
      UsuarioJpaRepository.java
      RepositorioUsuariosPostgre.java
  /api
    /dto
      CrearUsuarioRequest.java
      EditarPerfilUsuarioRequest.java
      UsuarioResponse.java
      ...
    UsuariosController.java
  /security
    SecurityConfig.java
  /exceptions
    GlobalExceptionHandler.java
```

## ✅ Principios DDD implementados

- [x] **Aggregate Root**: `Usuario` controla su propio ciclo de vida
- [x] **Value Objects**: Todos los atributos son VOs inmutables con validación
- [x] **Ubiquitous Language**: Nombres consistentes (español en dominio)
- [x] **Factory Methods**: `Usuario.create()`, `Usuario.reconstitute()`
- [x] **Encapsulación**: Solo métodos de comportamiento, no setters
- [x] **Invariantes protegidos**: Validación en constructores privados de VOs
- [x] **Excepciones de dominio**: Errores específicos del negocio

## ✅ Arquitectura Hexagonal implementada

- [x] **Dominio independiente**: Sin dependencias externas
- [x] **Ports**: Interface `RepositorioUsuarios` en domain
- [x] **Adapters**: `RepositorioUsuariosPostgre`, `UsuariosController`
- [x] **Anti-Corruption Layer**: `UsuarioMapper` traduce entre capas
- [x] **Dependency Inversion**: Dominio define interfaces, infrastructure implementa

## ✅ Dependencias entre capas (validado)

```text
infrastructure → application → domain
     ↑              ↑
     NO             NO
```

- [x] `domain` NO importa nada de `application` ni `infrastructure`
- [x] `application` solo importa de `domain`
- [x] `infrastructure` puede importar de `domain` y `application`
- [x] No hay ciclos de dependencia

## ✅ Testing strategy implementada

- [x] **Unit tests de dominio**: Sin Spring, sin BD
- [x] **Tests de Value Objects**: Validaciones completas
- [x] **Tests de comportamiento**: Métodos de Usuario
- [x] **Tests de casos de uso**: Con mocks de repositorios (Mockito)
- [ ] **Tests de integración**: Spring Boot Test con H2 (parcial)
- [ ] **Tests de API**: MockMvc (pendiente)

## ✅ Flujo de datos implementado

```text
HTTP Request
    ↓
UsuariosController (REST Adapter) - infrastructure
    ↓
Request DTO → Command
    ↓
Use Case (Application Service) - application
    ↓
Usuario (Domain Entity)
    ↓
RepositorioUsuarios (Port interface) - domain
    ↓
RepositorioUsuariosPostgre (JPA Adapter) - infrastructure
    ↓
UsuarioMapper + UsuarioEntity
    ↓
H2 / PostgreSQL
```

## 📋 Eventos de dominio (pendiente)

- [ ] Interface `IEventosPublisher` en application
- [ ] Implementación RabbitMQ en infrastructure
- [ ] Eventos: `UsuarioCreado`, `EmailVerificado`, `ContrasenaRestablecida`

- [ ] Implementar patrón Domain Events.
- [ ] `Usuario` publica eventos internos.
- [ ] Casos de uso los consumen y publican a mensajería.
- [ ] Desacoplamiento entre microservicios vía eventos.

## ⛔ Antipatrones a evitar

- [x] **Anemic Domain Model**: Usuario tiene comportamiento, no solo getters/setters.
- [x] **Smart UI**: Lógica en dominio, no en controladores.
- [ ] **God Objects**: Separar responsabilidades en casos de uso distintos.
- [ ] **Leaky Abstractions**: No exponer UsuarioEntity fuera de infrastructure.
- [ ] **Transaction Script**: Usar objetos de dominio, no servicios procedurales.
