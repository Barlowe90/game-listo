# Architecture Checklist – Hexagonal + DDD

## ✅ Estructura de carpetas completada

```text
/domain
  /usuario
    Usuario.java (Aggregate Root)
    UsuarioId.java, Email.java, Username.java (Value Objects)
    PasswordHash.java, Avatar.java (Value Objects)
    DiscordUserId.java, DiscordUsername.java (Value Objects)
    Rol.java, Idioma.java, EstadoUsuario.java (Enums)
  /repositories
    RepositorioUsuarios.java (Port - pendiente)
  /errors
    EntidadNoEncontrada.java

/application
  IEventosPublisher.java (Port - pendiente)
  /services
    [Casos de uso - pendientes]

/infrastructure
  /persistence/postgres
    /entity
      UsuarioEntity.java (Adapter JPA)
    /mapper
      UsuarioMapper.java (Anti-Corruption Layer)
    /repository
      [Implementación JpaRepository - pendiente]
  /api/rest
    [Controladores REST - pendientes]
  /messaging
    [Publishers y Listeners - pendientes]
  /security
    [Configuración JWT - pendiente]
```

## ✅ Principios DDD implementados

- [x] **Aggregate Root**: `Usuario` controla su propio ciclo de vida.
- [x] **Value Objects**: Todos los atributos del usuario son VOs inmutables.
- [x] **Ubiquitous Language**: Nombres en español/inglés consistentes.
- [x] **Factory Methods**: `Usuario.create()`, `Usuario.reconstitute()`.
- [x] **Encapsulación**: No setters en dominio, solo métodos de comportamiento.
- [x] **Invariantes protegidos**: Validación en constructores privados de VOs.

## ✅ Arquitectura Hexagonal implementada

- [x] **Dominio independiente**: Sin dependencias externas.
- [x] **Ports**: Interfaces de repositorio (pendiente) y eventos (pendiente) en dominio/application.
- [x] **Adapters**: UsuarioEntity + UsuarioMapper en infrastructure.
- [x] **Anti-Corruption Layer**: UsuarioMapper traduce entre capas.
- [x] **Dependency Inversion**: Dominio define interfaces, infrastructure implementa.

## 📋 Dependencias entre capas (pendiente validar)

``` text
infrastructure → application → domain
     ↑              ↑
     NO            NO
```

- [ ] `domain` NO importa nada de `application` ni `infrastructure`.
- [ ] `application` solo importa de `domain`.
- [ ] `infrastructure` puede importar de `domain` y `application`.
- [ ] No hay ciclos de dependencia.

## 📋 Testing strategy (pendiente)

- [ ] **Unit tests de dominio**: Sin Spring, sin BD.
- [ ] **Tests de Value Objects**: Validaciones.
- [ ] **Tests de comportamiento**: Métodos de Usuario.
- [ ] **Tests de casos de uso**: Con mocks de repositorios.
- [ ] **Tests de integración**: Spring Boot Test con BD embebida.
- [ ] **Tests de API**: MockMvc o REST Assured.

## ✅ Flujo de datos implementado

``` text
HTTP Request
    ↓
Controller (REST Adapter) - infrastructure
    ↓
DTO → Command/Query
    ↓
Caso de Uso (Application Service) - application
    ↓
Domain Entity (Usuario)
    ↓
Repository Port (interface) - domain
    ↓
Repository Adapter (JPA) - infrastructure
    ↓
UsuarioEntity + UsuarioMapper
    ↓
PostgreSQL
```

## 📋 Eventos de dominio (pendiente)

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
