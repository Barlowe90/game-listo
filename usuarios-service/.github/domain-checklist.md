# Domain Checklist – Microservicio Usuarios

## Modelado completado

- [x] Usuario con ID inmutable (`UsuarioId` final)
- [x] Email y Username como Value Objects
- [x] Value Objects sin Lombok (inmutables, con validación)
- [x] Validación estricta en constructores de VOs
- [x] `PasswordHash`, `Avatar`, `DiscordUserId`, `DiscordUsername` como VOs
- [x] `TokenVerificacion` para verificación de email y reset de contraseña
- [x] Enums: `Rol`, `Idioma`, `EstadoUsuario`

## Invariantes implementados

- [x] Email válido obligatorio (validación regex, normalizado a minúsculas)
- [x] Username válido (3-30 caracteres alfanuméricos)
- [x] Fecha de creación no nula
- [x] Estado por defecto: `PENDIENTE_DE_VERIFICACION` (al crear)
- [x] Token de verificación generado automáticamente al crear usuario
- [x] Token expira en 24 horas

## Métodos de dominio

### Factory Methods

- [x] `Usuario.create()` - Crea usuario nuevo con token de verificación
- [x] `Usuario.reconstitute()` - Reconstruye desde persistencia

### Comportamiento de perfil

- [x] `changeUsername()`, `changeEmail()`, `changePasswordHash()`
- [x] `changeAvatar()`, `changeLanguage()`
- [x] `enableNotifications()`, `disableNotifications()`

### Gestión de estado

- [x] `suspend()`, `activate()`, `delete()`
- [x] `marcarPendienteVerificacion()` - Cambia estado al cambiar email
- [x] `isActive()`, `isSuspended()`, `isDeleted()`

### Verificación de email

- [x] `generarTokenVerificacion()` - Genera nuevo token con expiración 24h
- [x] `verificarEmail(token)` - Verifica y activa usuario
- [x] `isTokenVerificacionExpirado()`, `isPendienteDeVerificacion()`

### Restablecimiento de contraseña

- [x] `generarTokenRestablecimiento()` - Genera token de reset con expiración 1h
- [x] `tieneTokenRestablecimientoValido(token)` - Valida token de reset
- [x] `invalidarTokenRestablecimiento()` - Limpia token tras uso

### Discord

- [x] `linkDiscord(discordUserId, discordUsername)`
- [x] `unlinkDiscord()`
- [x] `hasDiscordLinked()`

## Eventos de dominio

- [x] `UsuarioCreado` - Publicado al crear un usuario
- [x] `EmailVerificado` - Publicado al verificar el email
- [x] `UsuarioEliminado` - Publicado al eliminar un usuario
- [x] `UsuarioActiviaNotificaciones` - Publicado al activar notificaciones
- [x] `UsuarioDesactivaNotificaciones` - Publicado al desactivar notificaciones
- [x] Eventos como records inmutables con timestamp

## Repositorio implementado

- [x] Interface `RepositorioUsuarios` en `/domain/repositories/`
- [x] Métodos: `save()`, `findById()`, `findByEmail()`, `findByUsername()`
- [x] Métodos: `existsByUsername()`, `existsByEmail()`
- [x] Métodos: `findByTokenVerificacion()`, `findByDiscordUserId()`
- [x] Métodos: `findByStatus()`, `findByStatusAndNotificationsActive()`
- [x] Método: `findAll()`
- [x] Sin anotaciones Spring/JPA
- [x] Retorna `Optional<Usuario>` o `List<Usuario>`

## Excepciones de dominio

- [x] `EntidadNoEncontrada` - Entidad genérica no encontrada
- [x] `UsuarioNoEncontradoException` - Usuario no encontrado por ID
- [x] `UsernameYaExisteException` - Username duplicado
- [x] `EmailYaRegistradoException` - Email duplicado
- [x] `TokenVerificacionInvalidoException` - Token de verificación expirado o inválido
- [x] `UsuarioYaVerificadoException` - Usuario ya verificado
- [x] `DiscordYaVinculadoException` - Cuenta de Discord ya vinculada

## Código prohibido en /domain (verificado)

- [x] Sin Spring (`@Component`, `@Service`, etc.)
- [x] Sin JPA (`@Entity`, `@Table`, etc.)
- [x] Sin DTOs
- [x] Sin logs (Logger)
- [x] Sin dependencias de infraestructura
