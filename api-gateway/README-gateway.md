# API Gateway - GameListo

## Descripción

API Gateway para la plataforma GameListo. Actúa como puerta de entrada única para todos los microservicios.

## Responsabilidades

### Implementado

1. **Enrutamiento de peticiones**: Direcciona peticiones a los microservicios correspondientes
2. **Validación de tokens JWT**: Verifica firma, expiración y claims en todas las rutas protegidas
3. **Revocación de tokens**: Verifica blacklist de tokens revocados usando Redis
4. **Rate Limiting**: Limita peticiones por IP usando Redis (100 peticiones/minuto)
5. **CORS**: Gestión de políticas CORS para aplicaciones frontend
6. **Enriquecimiento de headers**: Agrega información del usuario autenticado a los headers para microservicios

### IMPORTANTE

- **Este servicio NO genera tokens JWT**, solo los valida
- La generación de tokens se realiza en `usuarios-service`
- El secreto JWT debe ser el mismo en Gateway y usuarios-service (en application.properties)

## Arquitectura

### Filtros Globales

```
Petición → RateLimitFilter (-50) → JwtAuthenticationFilter (-100) → Enrutamiento → Microservicio
```

#### 1. JwtAuthenticationFilter (Order: -100)

- Extrae token del header `Authorization: Bearer <token>`
- Valida firma y expiración usando `JwtValidator`
- Verifica que no esté revocado en Redis
- Agrega headers con info del usuario:
    - `X-User-Id`: ID del usuario
    - `X-User-Username`: Nombre de usuario
    - `X-User-Email`: Email del usuario
    - `X-User-Roles`: Roles (separados por comas)
- Permite paso libre a rutas públicas

#### 2. RateLimitFilter (Order: -50)

- Cuenta peticiones por IP en Redis
- Límite: 100 peticiones por minuto
- Retorna `429 Too Many Requests` si se excede
- Fail-open: si Redis falla, permite la petición

### Rutas

#### Públicas (sin JWT)

- `POST /v1/usuarios/auth/register` - Registro de nuevos usuarios
- `POST /v1/usuarios/auth/verify-email` - Verificar email con token
- `POST /v1/usuarios/auth/resend-verification` - Reenviar email de verificación
- `POST /v1/usuarios/auth/forgot-password` - Solicitar restablecimiento de contraseña
- `POST /v1/usuarios/auth/reset-password` - Restablecer contraseña con token
- `POST /v1/usuarios/auth/login` - Login
- `POST /v1/usuarios/auth/refresh` - Renovar access token
- `GET /v1/usuarios/health` - Health check

#### Protegidas (requieren JWT)

- `GET /v1/usuarios/**` - Todas las demás rutas de usuarios
- Futuras rutas de otros microservicios

## Configuración

### Redis

El Gateway usa Redis para:

1. **Rate Limiting**: `rate_limit:<IP>` → contador con TTL de 1 minuto
2. **Token Revocation**: `revoked:jti:<JTI>` → marcador con TTL según expiración del token

## Ejecución

### Docker

```bash
# Desde la raíz del proyecto
docker-compose up -d
```

## Flujo de Autenticación

1. Cliente hace login en `POST /v1/usuarios/auth/login` (ruta pública)
2. `usuarios-service` genera access token + refresh token
3. Cliente incluye access token en header `Authorization: Bearer <token>`
4. Gateway valida token en `JwtAuthenticationFilter`:
    - Verifica firma con el secreto compartido
    - Verifica que no haya expirado
    - Verifica que no esté en blacklist (Redis)
5. Gateway agrega headers con info del usuario
6. Microservicio recibe petición con headers `X-User-*`
