# Vincular Cuenta de Discord

Este documento describe el flujo para vincular una cuenta de Discord a un usuario en GameListo.

## Flujo de Vinculación

### 1. Configuración Inicial

Primero, debes registrar tu aplicación en Discord Developer Portal:

1. Ve a [Discord Developer Portal](https://discord.com/developers/applications)
2. Crea una nueva aplicación o selecciona una existente
3. En la sección "OAuth2", agrega las URLs de redirección permitidas:
   - Desarrollo: `http://localhost:3000/discord/callback`
   - Producción: `https://gamelisto.com/discord/callback`
4. Copia el `Client ID` y `Client Secret`
5. Configura las variables de entorno:

   ```bash
   export DISCORD_CLIENT_ID=tu_client_id
   export DISCORD_CLIENT_SECRET=tu_client_secret
   ```

### 2. Iniciar Flujo de Autorización (Frontend)

El frontend debe redirigir al usuario a la URL de autorización de Discord:

```text
https://discord.com/oauth2/authorize?
  response_type=code&
  client_id=TU_CLIENT_ID&
  scope=identify&
  state=RANDOM_STATE&
  redirect_uri=https://gamelisto.com/discord/callback&
  prompt=consent
```

**Parámetros:**

- `response_type=code`: Tipo de flujo OAuth2
- `client_id`: ID de tu aplicación de Discord
- `scope=identify`: Scope para obtener información básica del usuario
- `state`: String aleatorio para prevenir CSRF (recomendado)
- `redirect_uri`: URL a la que Discord redirigirá después de la autorización
- `prompt=consent`: Fuerza al usuario a aprobar los permisos cada vez

### 3. Callback de Discord

Después de que el usuario autorice, Discord redirige de vuelta con:

```text
https://gamelisto.com/discord/callback?code=AUTHORIZATION_CODE&state=RANDOM_STATE
```

**Validaciones en el frontend:**

1. Verificar que el `state` coincida con el generado en el paso 2
2. Extraer el `code` de la URL
3. Enviar el `code` al backend

### 4. Vincular Discord (Backend)

**Endpoint:** `POST /v1/usuarios/user/{id}/discord/link`

**Headers:**

```text
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
```

**Request Body:**

```json
{
  "code": "AUTHORIZATION_CODE_FROM_DISCORD",
  "redirectUri": "https://gamelisto.com/discord/callback"
}
```

**Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "player123",
  "email": "player@example.com",
  "avatar": "https://cdn.gamelisto.com/avatars/default.png",
  "language": "ESP",
  "notificationsActive": true,
  "status": "ACTIVO",
  "discordUserId": "123456789012345678",
  "discordUsername": "DiscordUser#1234",
  "discordLinkedAt": "2023-12-13T10:30:00Z",
  "discordConsent": true,
  "createdAt": "2023-01-15T08:00:00Z",
  "updatedAt": "2023-12-13T10:30:00Z"
}
```

**Errores Posibles:**

- **400 Bad Request:** Código de autorización inválido o expirado

  ```json
  {
    "error": "El código de autorización no es válido",
    "status": 400,
    "timestamp": "2023-12-13T10:30:00Z"
  }
  ```

- **404 Not Found:** Usuario no encontrado

  ```json
  {
    "error": "Usuario no encontrado con ID: 550e8400-e29b-41d4-a716-446655440000",
    "status": 404,
    "timestamp": "2023-12-13T10:30:00Z"
  }
  ```

- **409 Conflict:** Cuenta de Discord ya vinculada a otro usuario

  ```json
  {
    "error": "La cuenta de Discord con ID '123456789012345678' ya está vinculada a otro usuario",
    "status": 409,
    "timestamp": "2023-12-13T10:30:00Z"
  }
  ```

- **502 Bad Gateway:** Error al comunicarse con Discord

  ```json
  {
    "error": "Error al comunicarse con Discord. Intenta nuevamente.",
    "status": 502,
    "timestamp": "2023-12-13T10:30:00Z"
  }
  ```

### 5. Desvincular Discord

**Endpoint:** `DELETE /v1/usuarios/user/{id}/discord/unlink`

**Headers:**

```text
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "player123",
  "email": "player@example.com",
  "avatar": "https://cdn.gamelisto.com/avatars/default.png",
  "language": "ESP",
  "notificationsActive": true,
  "status": "ACTIVO",
  "discordUserId": null,
  "discordUsername": null,
  "discordLinkedAt": null,
  "discordConsent": false,
  "createdAt": "2023-01-15T08:00:00Z",
  "updatedAt": "2023-12-13T10:35:00Z"
}
```

## Diagrama de Secuencia

```text
Frontend          Backend          Discord API
   |                 |                  |
   |--- Redirect --->|                  |
   |                 |                  |
   |                 |<-- Authorize ----|
   |                 |                  |
   |<-- Callback ----|                  |
   |  (with code)    |                  |
   |                 |                  |
   |--- POST /link ->|                  |
   |  (with code)    |                  |
   |                 |--- Exchange ---->|
   |                 |    Code          |
   |                 |<-- Access -------|
   |                 |    Token         |
   |                 |                  |
   |                 |--- Get User ---->|
   |                 |    Info          |
   |                 |<-- User Data ----|
   |                 |                  |
   |<-- Response ----|                  |
   |  (with Discord  |                  |
   |   info)         |                  |
```

## Implementación en el Frontend (React)

### Hook personalizado

```typescript
// hooks/useDiscordLink.ts
import { useState } from 'react';

export const useDiscordLink = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const startDiscordAuth = (userId: string) => {
    const clientId = process.env.NEXT_PUBLIC_DISCORD_CLIENT_ID;
    const redirectUri = `${window.location.origin}/discord/callback`;
    const state = generateRandomState();
    
    // Guardar state en sessionStorage para validar después
    sessionStorage.setItem('discord_oauth_state', state);
    
    const authUrl = `https://discord.com/oauth2/authorize?${new URLSearchParams({
      response_type: 'code',
      client_id: clientId,
      scope: 'identify',
      state: state,
      redirect_uri: redirectUri,
      prompt: 'consent'
    })}`;
    
    window.location.href = authUrl;
  };

  const linkDiscord = async (userId: string, code: string, redirectUri: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/v1/usuarios/user/${userId}/discord/link`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({ code, redirectUri })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al vincular Discord');
      }

      const user = await response.json();
      return user;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const unlinkDiscord = async (userId: string) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/v1/usuarios/user/${userId}/discord/unlink`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${getToken()}`
        }
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al desvincular Discord');
      }

      const user = await response.json();
      return user;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { loading, error, startDiscordAuth, linkDiscord, unlinkDiscord };
};

function generateRandomState() {
  return Math.random().toString(36).substring(2, 15);
}

function getToken() {
  // Implementar según tu estrategia de autenticación
  return localStorage.getItem('jwt_token');
}
```

### Página de callback

```typescript
// pages/discord/callback.tsx
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useDiscordLink } from '@/hooks/useDiscordLink';
import { useAuth } from '@/hooks/useAuth';

export default function DiscordCallback() {
  const router = useRouter();
  const { linkDiscord } = useDiscordLink();
  const { user } = useAuth();

  useEffect(() => {
    const handleCallback = async () => {
      const { code, state } = router.query;
      const savedState = sessionStorage.getItem('discord_oauth_state');

      // Validar state para prevenir CSRF
      if (state !== savedState) {
        console.error('Estado inválido');
        router.push('/settings?error=invalid_state');
        return;
      }

      if (code && user) {
        try {
          const redirectUri = `${window.location.origin}/discord/callback`;
          await linkDiscord(user.id, code as string, redirectUri);
          
          // Limpiar state
          sessionStorage.removeItem('discord_oauth_state');
          
          // Redirigir a configuración con éxito
          router.push('/settings?discord=linked');
        } catch (error) {
          console.error('Error al vincular Discord:', error);
          router.push('/settings?error=link_failed');
        }
      }
    };

    if (router.isReady) {
      handleCallback();
    }
  }, [router.isReady, router.query, linkDiscord, user]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <h2 className="text-2xl font-bold mb-4">Vinculando Discord...</h2>
        <p>Por favor espera mientras procesamos tu solicitud.</p>
      </div>
    </div>
  );
}
```

### Componente de perfil

```typescript
// components/DiscordSection.tsx
import { useDiscordLink } from '@/hooks/useDiscordLink';
import { useAuth } from '@/hooks/useAuth';

export default function DiscordSection() {
  const { user, refreshUser } = useAuth();
  const { loading, error, startDiscordAuth, unlinkDiscord } = useDiscordLink();

  const handleLink = () => {
    if (user) {
      startDiscordAuth(user.id);
    }
  };

  const handleUnlink = async () => {
    if (user && confirm('¿Estás seguro de que quieres desvincular tu cuenta de Discord?')) {
      try {
        await unlinkDiscord(user.id);
        await refreshUser();
      } catch (error) {
        console.error('Error al desvincular:', error);
      }
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h3 className="text-xl font-bold mb-4">Discord</h3>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {user?.discordUsername ? (
        <div>
          <p className="mb-2">
            Cuenta vinculada: <strong>{user.discordUsername}</strong>
          </p>
          <button
            onClick={handleUnlink}
            disabled={loading}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 disabled:opacity-50"
          >
            {loading ? 'Desvinculando...' : 'Desvincular Discord'}
          </button>
        </div>
      ) : (
        <div>
          <p className="mb-4 text-gray-600">
            Vincula tu cuenta de Discord para mostrar tu perfil en la comunidad.
          </p>
          <button
            onClick={handleLink}
            disabled={loading}
            className="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700 disabled:opacity-50"
          >
            {loading ? 'Procesando...' : 'Vincular Discord'}
          </button>
        </div>
      )}
    </div>
  );
}
```

## Notas Importantes

1. **Seguridad:**
   - Siempre valida el parámetro `state` para prevenir ataques CSRF
   - Nunca expongas el `client_secret` en el frontend
   - Los tokens de Discord expiran en 7 días por defecto

2. **Experiencia de Usuario:**
   - Muestra un loader mientras se procesa la vinculación
   - Proporciona mensajes de error claros
   - Confirma antes de desvincular la cuenta

3. **Rate Limiting:**
   - Discord tiene rate limits estrictos
   - Implementa retry logic con backoff exponencial si es necesario

4. **Scopes:**
   - `identify`: Información básica del usuario (ID, username, avatar)
   - `email`: Dirección de email (requiere aprobación adicional)
   - `guilds`: Lista de servidores del usuario (requiere aprobación adicional)

## Referencias

- [Discord OAuth2 Documentation](https://discord.com/developers/docs/topics/oauth2)
- [Discord User Resource](https://discord.com/developers/docs/resources/user)
- [RFC 6749 - OAuth 2.0](https://datatracker.ietf.org/doc/html/rfc6749)
