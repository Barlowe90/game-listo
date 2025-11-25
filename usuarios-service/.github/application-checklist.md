# Application Checklist – Casos de Uso

## 📋 Estructura de casos de uso

- [ ] Cada caso de uso en `/application/services/`.
- [ ] Anotados con `@Service` (solo capa application).
- [ ] Inyección de `RepositorioUsuarios` por constructor.
- [ ] Inyección de `IEventosPublisher` por constructor.
- [ ] Sin acceso directo a JPA repositories.

## 📋 Casos de uso principales

### Gestión de perfil

- [ ] `ConsultarPerfilPropio` - GET /me
- [ ] `ConsultarPerfilPublico` - GET /user/{id}
- [ ] `EditarPerfil` - PATCH /user/{id}
- [ ] `CambiarAvatar` - parte de EditarPerfil
- [ ] `CambiarIdioma` - parte de EditarPerfil

### Sincronización desde Auth

- [ ] `SincronizarEmailDesdeAuth` - Listener de eventos
- [ ] `SincronizarPasswordDesdeAuth` - Listener de eventos
- [ ] `SincronizarVerificacionEmail` - Listener de eventos

### Gestión de estado

- [ ] `SuspenderUsuario` - PATCH /user/{id}/state (admin)
- [ ] `ActivarUsuario` - PATCH /user/{id}/state (admin)
- [ ] `EliminarUsuario` - DELETE /user/{id}

### Búsqueda

- [ ] `BuscarUsuarios` - GET /users?search=...

### Discord

- [ ] `VincularDiscord` - POST /discord/link
- [ ] `DesvincularDiscord` - DELETE /discord/link

## 📋 DTOs de aplicación

- [ ] Command objects para entrada: `EditarPerfilCommand`, `BuscarUsuariosQuery`.
- [ ] DTOs de salida: `PerfilUsuarioDTO`, `PerfilPublicoDTO`.
- [ ] Validación con Bean Validation (`@NotNull`, `@Email`, etc.).
- [ ] Conversión DTO ↔ Domain en el caso de uso.

## 📋 Publicación de eventos

- [ ] Interface `IEventosPublisher` en `/application/`.
- [ ] Implementación en `/infrastructure/messaging/`.
- [ ] Eventos: `UsuarioCreado`, `UsuarioActualizado`, `PerfilEditado`, `UsuarioEliminado`.
- [ ] Los casos de uso publican eventos después de cambios exitosos.

## ✅ Reglas implementadas

- [x] Casos de uso coordinan flujo, no ejecutan lógica de negocio.
- [x] Lógica de negocio en entidades de dominio (`Usuario`).
- [x] No conocen detalles de API REST (sin `ResponseEntity`, `HttpServletRequest`).
- [x] Retornan objetos de dominio o DTOs simples.
- [x] Usan `Optional` para valores que pueden no existir.

## ⛔ Prohibiciones en /application

- [ ] No usar entities JPA (`UsuarioEntity`).
- [ ] No usar anotaciones REST (`@RestController`, `@GetMapping`).
- [ ] No manejar HTTP status codes directamente.
- [ ] No lanzar excepciones HTTP (usar excepciones de dominio).
- [ ] No acceder directamente a bases de datos.
