# Infrastructure Checklist – Microservicio Usuarios

## ✅ Persistencia completada

- [x] `UsuarioEntity` en `/infrastructure/persistence/postgres/entity/`.
- [x] Entity como POJO puro con anotaciones JPA.
- [x] Sin lógica de negocio en UsuarioEntity.
- [x] Solo importa enums del dominio (Rol, Idioma, EstadoUsuario).
- [x] Nombres de columnas en inglés: `created_at`, `updated_at`, `role`, `language`, `status`.

## ✅ Mapper (Anti-Corruption Layer)

- [x] `UsuarioMapper` en `/infrastructure/persistence/postgres/mapper/`.
- [x] Anotado con `@Component`.
- [x] `toEntity()`: Usuario → UsuarioEntity.
- [x] `toDomain()`: UsuarioEntity → Usuario.
- [x] Extrae valores de VOs y reconstruye con `Usuario.reconstitute()`.
- [x] Maneja campos opcionales (avatar, discord).

## 📋 Repositorio JPA (pendiente)

- [ ] Interface `UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID>`.
- [ ] Implementación `UsuarioRepositorioPostgres implements RepositorioUsuarios`.
- [ ] Usa `UsuarioMapper` para conversión.
- [ ] Query methods: `findByEmail()`, `existsByUsername()`.
- [ ] Custom query para búsqueda por fragmento de username.

## 📋 Controladores REST (pendiente)

- [ ] Base path: `/v1/usuarios`.
- [ ] DTOs en `/infrastructure/api/dto/`.
- [ ] Request/Response separados.
- [ ] `@RestController` + `@RequestMapping`.
- [ ] Validación con `@Valid`.
- [ ] Inyecta casos de uso, no repositorios.

## 📋 Seguridad (pendiente)

- [ ] `SecurityConfig` configura JWT validation.
- [ ] Extrae `userId` del token.
- [ ] `@PreAuthorize` donde corresponda.
- [ ] Usuario solo puede editar su propio perfil.

## 📋 Manejo de errores (pendiente)

- [ ] `@RestControllerAdvice` para excepciones.
- [ ] Mapea excepciones de dominio a HTTP status.
- [ ] Respuestas de error estandarizadas.

## 📋 Mensajería (pendiente)

- [ ] Publishers en `/infrastructure/messaging/publishers/`.
- [ ] Listeners en `/infrastructure/messaging/listeners/`.
- [ ] Config en `/infrastructure/messaging/config/`.
- [ ] Eventos: `UsuarioCreado`, `UsuarioActualizado`, `EmailCambiado`.
