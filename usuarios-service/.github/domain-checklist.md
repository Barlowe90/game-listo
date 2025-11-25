# Domain Checklist – Microservicio Usuarios

## ✅ Modelado completado

- [x] Usuario con ID inmutable (UsuarioId final).
- [x] Email y Username como Value Objects.
- [x] Value Objects sin Lombok.
- [x] Validación estricta en constructores de VOs.
- [x] PasswordHash, Avatar, DiscordUserId, DiscordUsername como VOs.
- [x] Enums: Rol, Idioma, EstadoUsuario.

## ✅ Invariantes implementados

- [x] Email válido obligatorio (validación regex).
- [x] Username válido (3-30 caracteres alfanuméricos).
- [x] Fecha de creación no nula.
- [x] Estado por defecto: ACTIVO.

## ✅ Métodos de dominio

- [x] Factory method: `Usuario.create()`.
- [x] Reconstitution: `Usuario.reconstitute()`.
- [x] Comportamiento: `changeUsername()`, `changeEmail()`, `suspend()`, `activate()`, etc.
- [x] Queries de estado: `isActive()`, `isSuspended()`, `isDeleted()`.

## 📋 Repositorio (pendiente implementar)

- [ ] Interface `RepositorioUsuarios` en `/domain/repositories/`.
- [ ] Métodos: `save()`, `findById()`, `findByEmail()`, `existsByUsername()`, `searchByUsernameFragment()`.
- [ ] Sin anotaciones Spring/JPA.
- [ ] Retorna `Optional<Usuario>` o `List<Usuario>`.

## ⛔ Código prohibido en /domain

- [x] Sin Spring (`@Component`, `@Service`, etc.).
- [x] Sin JPA (`@Entity`, `@Table`, etc.).
- [x] Sin DTOs.
- [x] Sin logs (Logger).
- [x] Sin dependencias de infraestructura.
