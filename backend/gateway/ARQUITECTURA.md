# 🏗️ Arquitectura del API Gateway - GameListo

## 📐 Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENTE (Frontend)                          │
│                    http://localhost:3000                            │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │ HTTP Request
                             │ Authorization: Bearer <JWT>
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Puerto 8080)                        │
│                    Spring Cloud Gateway                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌────────────────────────────────────────────────────────────┐   │
│  │  1️⃣  RateLimitFilter (Order: -50)                          │   │
│  │      • Cuenta peticiones por IP en Redis                   │   │
│  │      • Límite: 100 peticiones/minuto                       │   │
│  │      • Key: rate_limit:<IP>                                │   │
│  │      • Si excede → 429 Too Many Requests                   │   │
│  └──────────────────────┬─────────────────────────────────────┘   │
│                         │ OK, continuar                            │
│                         ▼                                           │
│  ┌────────────────────────────────────────────────────────────┐   │
│  │  2️⃣  JwtAuthenticationFilter (Order: -100)                 │   │
│  │                                                            │   │
│  │      ¿Es ruta pública?                                     │   │
│  │      • /v1/usuarios/auth/login ─────────────────┐         │   │
│  │      • /v1/usuarios/registro                    │         │   │
│  │      • /actuator/health                         │         │   │
│  │                                                  │         │   │
│  │      Si NO es pública:                          │         │   │
│  │      ┌─────────────────────────────────┐        │         │   │
│  │      │ a) Extraer token del header     │        │         │   │
│  │      │    Authorization: Bearer <JWT>  │        │         │   │
│  │      └─────────────┬───────────────────┘        │         │   │
│  │                    ▼                             │         │   │
│  │      ┌─────────────────────────────────┐        │         │   │
│  │      │ b) JwtValidator                 │        │         │   │
│  │      │    • Verificar firma            │        │         │   │
│  │      │    • Verificar expiración       │        │         │   │
│  │      │    • Extraer claims             │        │         │   │
│  │      └─────────────┬───────────────────┘        │         │   │
│  │                    ▼                             │         │   │
│  │      ┌─────────────────────────────────┐        │         │   │
│  │      │ c) TokenRevocationService       │        │         │   │
│  │      │    • Verificar en Redis         │        │         │   │
│  │      │    • Key: revoked:jti:<JTI>     │        │         │   │
│  │      │    • Si revocado → 401          │        │         │   │
│  │      └─────────────┬───────────────────┘        │         │   │
│  │                    ▼                             │         │   │
│  │      ┌─────────────────────────────────┐        │         │   │
│  │      │ d) Agregar headers              │        │         │   │
│  │      │    X-User-Id: <userId>          │◄───────┘         │   │
│  │      │    X-User-Roles: <rol>          │                  │   │
│  │      └─────────────┬───────────────────┘                  │   │
│  │                    │                                       │   │
│  └────────────────────┼───────────────────────────────────────┘   │
│                       │                                            │
│  ┌────────────────────┼───────────────────────────────────────┐   │
│  │  3️⃣  RouteLocator  │                                       │   │
│  │                    ▼                                       │   │
│  │      ┌──────────────────────────────┐                     │   │
│  │      │ ¿Qué ruta coincide?          │                     │   │
│  │      │                              │                     │   │
│  │      │ /v1/usuarios/** ──→ usuarios:8081         │   │
│  │      │ /v1/catalogo/** ──→ catalogo:8082         │   │
│  │      │ /v1/biblioteca/**→ biblioteca:8083        │   │
│  │      └──────────────┬───────────────┘                     │   │
│  │                     │                                      │   │
│  └─────────────────────┼──────────────────────────────────────┘   │
│                        │                                           │
└────────────────────────┼───────────────────────────────────────────┘
                         │ Proxied Request
                         │ + Headers enriquecidos
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      MICROSERVICIOS                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌───────────────────┐  ┌───────────────────┐  ┌────────────────┐ │
│  │ usuarios          │  │ catalogo          │  │ biblioteca     │ │
│  │    :8081          │  │    :8082          │  │    :8083       │ │
│  │                   │  │                   │  │                │ │
│  │ Recibe headers:   │  │                   │  │                │ │
│  │ X-User-Id         │  │                   │  │                │ │
│  │ X-User-Roles      │  │                   │  │                │ │
│  └───────────────────┘  └───────────────────┘  └────────────────┘ │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                           REDIS (:6379)                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  📊 Datos almacenados:                                             │
│                                                                     │
│  1. Rate Limiting                                                  │
│     rate_limit:192.168.1.100 = 45  (TTL: 60s)                     │
│     rate_limit:192.168.1.101 = 12  (TTL: 60s)                     │
│                                                                     │
│  2. Token Revocation                                               │
│     revoked:jti:abc123... = "revoked"  (TTL: 900s)                │
│     revoked:jti:def456... = "revoked"  (TTL: 900s)                │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## 🔐 Flujo de Autenticación Completo

### 1. Login (Ruta Pública)

```
Cliente → Gateway → usuarios
        /v1/usuarios/auth/login
        
        ✓ Sin validación JWT (ruta pública)
        ✓ Rate limiting aplicado
        
usuarios genera:
  • Access Token (JWT, 15 min)
  • Refresh Token (UUID, 7 días)
  
Cliente ← Gateway ← usuarios
        { "accessToken": "eyJ...", "refreshToken": "..." }
```

### 2. Petición Protegida

```
Cliente → Gateway
        /v1/usuarios/auth/me
        Authorization: Bearer eyJ...
        
Gateway valida:
  ✓ Firma del token
  ✓ Expiración
  ✓ No está revocado
  
Gateway agrega headers:
        X-User-Id: 123e4567-e89b...
        X-User-Roles: USER
  
Gateway → usuarios
        /v1/usuarios/auth/me
        + Headers X-User-*
        
usuarios:
  • Lee X-User-Id del header
  • NO necesita validar JWT (ya lo hizo Gateway)
  • Confía en los headers
  
Cliente ← Gateway ← usuarios
        { "id": "123e4567...", "username": "johndoe", ... }
```

### 3. Logout (Revocación)

```
Cliente → Gateway → usuarios
        /v1/usuarios/auth/logout
        Authorization: Bearer eyJ...
        
usuarios:
  • Extrae JTI del token
  • Agrega a Redis: revoked:jti:<JTI>
  • TTL = tiempo hasta expiración del token
  
usuarios → Redis
        SET revoked:jti:abc123... "revoked" EX 900
        
Cliente ← Gateway ← usuarios
        200 OK
        
Próxima petición con ese token:
  Gateway → Redis → ¿exists revoked:jti:abc123...?
  Redis → true
  Gateway → Cliente: 401 Unauthorized
```

## 📦 Componentes Implementados

### Config

- ✅ `JwtProperties` - Propiedades de configuración JWT

### Security

- ✅ `JwtValidator` - Validación de firma y claims
- ✅ `TokenRevocationService` - Blacklist de tokens
- ✅ `SecurityConfig` - Desactivar autenticación por defecto

### Filters

- ✅ `JwtAuthenticationFilter` - Validación y enriquecimiento
- ✅ `RateLimitFilter` - Límite de peticiones

### Tests

- ✅ `JwtValidatorTest` - Tests unitarios del validador

## 🎯 Puntos Clave

1. **Stateless**: El Gateway no mantiene sesiones
2. **Secreto compartido**: `jwt.secret` debe ser idéntico en Gateway y usuarios
3. **Fail-open**: Si Redis falla, el rate limiting permite peticiones (prioriza disponibilidad)
4. **Headers enriquecidos**: Los microservicios confían en `X-User-*` (red interna segura)
5. **Orden de filtros**: RateLimitFilter (-50) → JwtAuthenticationFilter (-100)
6. **TTL en Redis**: Los tokens revocados se limpian automáticamente cuando expiran

## 🚀 Ventajas de Esta Arquitectura

✅ **Centralización**: Un solo punto de autenticación  
✅ **Performance**: Microservicios no validan JWT (ya lo hizo Gateway)  
✅ **Seguridad**: Revocación inmediata con Redis  
✅ **Escalabilidad**: Gateway stateless puede replicarse  
✅ **Observabilidad**: Todo el tráfico pasa por un punto  
✅ **Rate limiting**: Protección DDoS a nivel de infraestructura  
