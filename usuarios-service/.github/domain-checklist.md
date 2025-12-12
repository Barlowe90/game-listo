# Domain Checklist – Microservicio Usuarios

## ✅ Modelado completado

- [x] Usuario con ID inmutable (`UsuarioId` final)
- [x] Email y Username como Value Objects
- [x] Value Objects sin Lombok (inmutables, con validación)
- [x] Validación estricta en constructores de VOs
- [x] `PasswordHash`, `Avatar`, `DiscordUserId`, `DiscordUsername` como VOs
- [x] `TokenVerificacion` para verificación de email y reset de contraseña
- [x] Enums: `Rol`, `Idioma`, `EstadoUsuario`

## ✅ Invariantes implementados

- [x] Email válido obligatorio (validación regex, normalizado a minúsculas)
- [x] Username válido (3-30 caracteres alfanuméricos)
- [x] Fecha de creación no nula
- [x] Estado por defecto: `PENDIENTE_DE_VERIFICACION` (al crear)
- [x] Token de verificación generado automáticamente al crear usuario
- [x] Token expira en 24 horas

## ✅ Métodos de dominio

### Factory Methods

- [x] `Usuario.create()` - Crea usuario nuevo con token de verificación
- [x] `Usuario.reconstitute()` - Reconstruye desde persistencia

### Comportamiento de perfil

- [x] `changeUsername()`, `changeEmail()`, `changePasswordHash()`
- [x] `changeAvatar()`, `changeLanguage()`
- [x] `enableNotifications()`, `disableNotifications()`

### Gestión de estado

- [x] `suspend()`, `activate()`, `delete()`
- [x] `isActive()`, `isSuspended()`, `isDeleted()`

### Verificación de email

- [x] `generarTokenVerificacion()` - Genera nuevo token con expiración 24h
- [x] `verificarEmail(token)` - Verifica y activa usuario
- [x] `isTokenVerificacionExpirado()`, `isPendienteDeVerificacion()`

### Restablecimiento de contraseña

- [x] `tieneTokenRestablecimientoValido(token)` - Valida token de reset
- [x] `invalidarTokenRestablecimiento()` - Limpia token tras uso

### Discord

- [x] `linkDiscord(discordUserId, discordUsername)`
- [x] `unlinkDiscord()`
- [x] `hasDiscordLinked()`

## ✅ Repositorio implementado

- [x] Interface `RepositorioUsuarios` en `/domain/repositories/`
- [x] Métodos: `save()`, `findById()`, `findByEmail()`, `findByUsername()`
- [x] Métodos: `existsByUsername()`, `existsByEmail()`
- [x] Métodos: `findByTokenVerificacion()`, `findAll()`
- [x] Sin anotaciones Spring/JPA
- [x] Retorna `Optional<Usuario>` o `List<Usuario>`

## ✅ Excepciones de dominio

- [x] `EntidadNoEncontrada` - Usuario no encontrado por ID
- [x] `UsernameYaExisteException` - Username duplicado
- [x] `EmailYaRegistradoException` - Email duplicado
- [x] `TokenInvalidoException` - Token expirado o inválido

## ⛔ Código prohibido en /domain (verificado)

- [x] Sin Spring (`@Component`, `@Service`, etc.)
- [x] Sin JPA (`@Entity`, `@Table`, etc.)
- [x] Sin DTOs
- [x] Sin logs (Logger)
- [x] Sin dependencias de infraestructura
