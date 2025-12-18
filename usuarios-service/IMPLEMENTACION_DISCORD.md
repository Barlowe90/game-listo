# Implementación del Endpoint de Vinculación de Discord

## Resumen

Se ha implementado exitosamente la funcionalidad para vincular y desvincular cuentas de Discord en el servicio de usuarios de GameListo. La implementación sigue los principios de Hexagonal Architecture y Domain-Driven Design del proyecto.

## Archivos Creados

### Capa de Dominio

- `domain/exceptions/DiscordYaVinculadoException.java` - Excepción cuando Discord ya está vinculado a otro usuario

### Capa de Aplicación

- `application/dto/VincularDiscordCommand.java` - Command para vincular Discord
- `application/usecases/VincularDiscordUseCase.java` - Caso de uso para vincular Discord
- `application/usecases/DesvincularDiscordUseCase.java` - Caso de uso para desvincular Discord

### Capa de Infraestructura

**API REST:**

- `infrastructure/api/dto/VincularDiscordRequest.java` - Request DTO para vincular Discord
- `infrastructure/api/exception/GlobalExceptionHandler.java` - Manejador global de excepciones

**Cliente Discord:**

- `infrastructure/discord/DiscordClient.java` - Cliente HTTP para Discord API
- `infrastructure/discord/DiscordTokenResponse.java` - Response del token OAuth2
- `infrastructure/discord/DiscordUserResponse.java` - Response de información del usuario
- `infrastructure/discord/DiscordApiException.java` - Excepción para errores de Discord API

**Configuración:**

- `infrastructure/config/RestTemplateConfig.java` - Bean de RestTemplate

## Archivos Modificados

### Repositorio

- `domain/repositories/RepositorioUsuarios.java` - Agregado método `findByDiscordUserId()`
- `infrastructure/persistence/postgres/repository/RepositorioUsuariosPostgre.java` - Implementación del método
- `infrastructure/persistence/postgres/repository/UsuarioJpaRepository.java` - Query method JPA

### Controller

- `infrastructure/api/rest/UsuariosController.java` - Agregados endpoints:
  - `POST /v1/usuarios/user/{id}/discord/link` - Vincular Discord
  - `DELETE /v1/usuarios/user/{id}/discord/unlink` - Desvincular Discord

### Configuración

- `src/main/resources/application.properties` - Agregadas propiedades Discord OAuth2

## Documentación

- `DISCORD_INTEGRATION.md` - Guía completa de integración con ejemplos de código

## Endpoints Implementados

### 1. Vincular Discord

```json
POST /v1/usuarios/user/{id}/discord/link
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}

Request Body:
{
  "code": "AUTHORIZATION_CODE",
  "redirectUri": "https://gamelisto.com/discord/callback"
}

Response (200 OK):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "player123",
  "discordUserId": "123456789012345678",
  "discordUsername": "DiscordUser#1234",
  "discordLinkedAt": "2023-12-13T10:30:00Z",
  "discordConsent": true,
  ...
}
```

### 2. Desvincular Discord

```json
DELETE /v1/usuarios/user/{id}/discord/unlink
Authorization: Bearer {JWT_TOKEN}

Response (200 OK):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "player123",
  "discordUserId": null,
  "discordUsername": null,
  "discordLinkedAt": null,
  "discordConsent": false,
  ...
}
```

## Flujo de Integración

1. **Frontend** redirige al usuario a Discord OAuth2
2. Discord solicita autorización al usuario
3. Discord redirige de vuelta con un `code`
4. **Frontend** envía el `code` al backend
5. **Backend** intercambia el `code` por un `access_token`
6. **Backend** obtiene información del usuario de Discord
7. **Backend** vincula la cuenta y retorna el usuario actualizado

## Validaciones Implementadas

- ✅ Usuario debe existir
- ✅ Código de autorización debe ser válido
- ✅ Cuenta de Discord no debe estar vinculada a otro usuario
- ✅ Validación de parámetros requeridos (@Valid)
- ✅ Manejo de errores de Discord API

## Excepciones Manejadas

| Excepción | HTTP Status | Descripción |
|-----------|-------------|-------------|
| `EntidadNoEncontrada` | 404 | Usuario no encontrado |
| `DiscordYaVinculadoException` | 409 | Discord ya vinculado a otro usuario |
| `DiscordApiException` | 502 | Error al comunicarse con Discord |
| `IllegalArgumentException` | 400 | Argumentos inválidos |
| `MethodArgumentNotValidException` | 400 | Error de validación |

## Configuración Requerida

Antes de usar esta funcionalidad, debes configurar las variables de entorno:

```bash
export DISCORD_CLIENT_ID=tu_client_id
export DISCORD_CLIENT_SECRET=tu_client_secret
```

O en `application.properties`:

```properties
discord.client-id=tu_client_id
discord.client-secret=tu_client_secret
```

## Testing

La implementación está lista para testing. Se recomienda crear tests para:

1. **Casos de éxito:**
   - Vincular Discord exitosamente
   - Desvincular Discord exitosamente

2. **Casos de error:**
   - Usuario no encontrado
   - Código inválido
   - Discord ya vinculado
   - Error de comunicación con Discord

## Próximos Pasos

1. Implementar tests unitarios e integración
2. Crear documentación OpenAPI/Swagger
3. Implementar refresh token handling (opcional)
4. Agregar logs estructurados para monitoreo
5. Implementar rate limiting para prevenir abuso
6. Agregar métricas de uso de la funcionalidad

## Referencias

- [Discord OAuth2 Documentation](https://discord.com/developers/docs/topics/oauth2)
- [Discord User Resource](https://discord.com/developers/docs/resources/user)
- Documentación completa en: `DISCORD_INTEGRATION.md`

## Notas Técnicas

### Arquitectura

- Sigue Hexagonal Architecture: `domain` → `application` → `infrastructure`
- Usa Value Objects para Discord: `DiscordUserId`, `DiscordUsername`
- Métodos de dominio: `Usuario.linkDiscord()`, `Usuario.unlinkDiscord()`

### Seguridad

- El `client_secret` nunca se expone al frontend
- Los tokens de Discord se intercambian server-side
- Se recomienda implementar validación de `state` en el frontend (CSRF protection)

### Performance

- RestTemplate hace llamadas síncronas (considerar WebClient para async)
- Rate limits de Discord: 50 requests por segundo por app
- Tokens expiran en 7 días (considera refresh tokens para sesiones largas)
