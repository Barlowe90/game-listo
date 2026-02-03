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

- `POST /v1/usuarios/auth/login` - Login
- `POST /v1/usuarios/auth/refresh` - Renovar token
- `POST /v1/usuarios/auth/register` - Registro
- `POST /v1/usuarios/auth/verificar-email` - Verificar email
- `POST /v1/usuarios/auth/resend-verification` - Reenviar verificación
- `POST /v1/usuarios/auth/forgot-password` - Restablecer contraseña
- `POST /v1/usuarios/auth/reset-password` - Resetear constraseña
- `GET /v1/usuarios/health` - Health check

#### Protegidas (requieren JWT)

- `GET /v1/usuarios/**` - Todas las demás rutas de usuarios
- Futuras rutas de otros microservicios

## Configuración

### Variables de Entorno

```properties
# JWT (debe coincidir con usuarios-service)
JWT_SECRET=secreto-super-seguro-en-produccion-debe-ser-muy-largo
jwt.expiration=900000  # 15 minutos
jwt.refresh-expiration=604800000  # 7 días
# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
# Puerto del Gateway
server.port=8090
```

### Redis

El Gateway usa Redis para:

1. **Rate Limiting**: `rate_limit:<IP>` → contador con TTL de 1 minuto
2. **Token Revocation**: `revoked:jti:<JTI>` → marcador con TTL según expiración del token

## Ejecución

### Local

```bash
# Asegúrate de tener Redis corriendo
# Desde el directorio api-gateway/
mvnw.cmd spring-boot:run
```

### Docker

```bash
# Desde la raíz del proyecto
docker-compose up -d
```

## Testing

### Probar rutas públicas

```bash
# Login (sin token)
curl -X POST http://localhost:8090/v1/usuarios/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Probar rutas protegidas

```bash
# Obtener perfil (con token)
curl http://localhost:8090/v1/usuarios/auth/me \
  -H "Authorization: Bearer <tu_token_jwt>"
```

### Probar rate limiting

```bash
# Ejecutar 101 peticiones rápidas
for i in {1..101}; do curl http://localhost:8090/v1/usuarios/health; done
# La petición 101 debería retornar 429 Too Many Requests
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

## Estructura

```
api-gateway/
├── config/
│   └── JwtProperties.java      # Propiedades JWT
├── security/
│   ├── JwtValidator.java       # Validación de tokens
│   ├── TokenRevocationService.java  # Blacklist de tokens
│   └── SecurityConfig.java     # Configuración Spring Security
├── filters/
│   ├── JwtAuthenticationFilter.java  # Filtro JWT
│   └── RateLimitFilter.java         # Filtro rate limiting
└── ApiGatewayApplication.java
```
