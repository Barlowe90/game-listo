# Autorización por Roles - usuarios-service

## Resumen de la Implementación

La autorización por roles en `usuarios-service` se implementa mediante **Spring Security** con `@PreAuthorize`,
confiando en la validación JWT realizada por el **API Gateway**.

## Flujo de Autenticación y Autorización

```
1. Usuario → Request con JWT → API Gateway
2. Gateway valida JWT (firma, expiración, revocación)
3. Gateway extrae claims del JWT (userId, username, email, roles)
4. Gateway agrega headers HTTP:
   - X-User-Id: "uuid-del-usuario"
   - X-User-Username: "john_doe"
   - X-User-Email: "john@example.com"
   - X-User-Roles: "USER,ADMIN"
5. Gateway enruta request → usuarios-service
6. GatewayAuthenticationFilter (usuarios-service) lee headers
7. GatewayAuthenticationFilter crea Authentication de Spring Security
8. @PreAuthorize valida permisos según roles
9. Controlador ejecuta la lógica de negocio
```

## Componentes Clave

### 1. **GatewayAuthenticationFilter** (usuarios-service)

**Ubicación:** `infrastructure/security/GatewayAuthenticationFilter.java`

**Responsabilidad:** Convertir headers HTTP en un objeto `Authentication` de Spring Security.

```java

@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(...) {
        String userId = request.getHeader("X-User-Id");
        String rolesHeader = request.getHeader("X-User-Roles"); // "USER,ADMIN"

        if (userId != null && rolesHeader != null) {
            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(rolesHeader.split(","))
                            .map(role -> "ROLE_" + role) // Spring requiere prefijo "ROLE_"
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            // El principal es el userId (para comparar con #id en @PreAuthorize)
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

**Detalles importantes:**

- El `principal` es el **userId** (UUID), no el username
- Los roles se prefijan con `ROLE_` (requerimiento de Spring Security)
- Si los headers no están presentes, la petición se trata como no autenticada

### 2. **SecurityConfig** (usuarios-service)

**Ubicación:** `infrastructure/security/SecurityConfig.java`

```java

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final GatewayAuthenticationFilter gatewayAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // Registrar filtro ANTES de UsernamePasswordAuthenticationFilter
                .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Permitir todas las peticiones - autorización se maneja con @PreAuthorize
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
```

**¿Por qué `permitAll()`?**

- La autenticación la hace el Gateway (validación JWT)
- La autorización se hace con `@PreAuthorize` en los controladores
- Esto permite endpoints públicos (login, registro) y protegidos en el mismo servicio

### 3. **JwtAuthenticationFilter** (API Gateway)

**Ubicación:** `api-gateway/filters/JwtAuthenticationFilter.java`

**Responsabilidad:** Validar JWT y agregar headers con información del usuario.

```java
// Token válido: agregar información del usuario a los headers
ServerHttpRequest mutatedRequest =
        exchange.getRequest()
                .mutate()
                .header("X-User-Id", jwtValidator.getUserId(claims))
                .header("X-User-Username", jwtValidator.getUsername(claims))
                .header("X-User-Email", jwtValidator.getEmail(claims))
                .header("X-User-Roles", String.join(",", jwtValidator.getRoles(claims)))
                .build();
```

## Uso de @PreAuthorize en Controladores

### Expresiones Comunes

#### 1. **Solo Administradores**

```java

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/users")
public ResponseEntity<List<UsuarioResponse>> obtenerTodos() {
    // Solo accesible para usuarios con rol ADMIN
}
```

#### 2. **Admin o el propio usuario**

```java

@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
@PutMapping("/{id}/password")
public ResponseEntity<Void> cambiarContrasena(@PathVariable String id, ...) {
    // Accesible para ADMIN o si el id coincide con el userId del token
}
```

**⚠️ Importante:** `authentication.principal` contiene el **userId** (UUID), no el username.

#### 3. **Cualquier usuario autenticado**

```java

@PreAuthorize("isAuthenticated()")
@GetMapping("/me")
public ResponseEntity<UsuarioResponse> getProfile() {
    // Requiere token válido, sin importar el rol
}
```

#### 4. **Múltiples roles**

```java
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@GetMapping("/reports")
public ResponseEntity<...>

getReports() {
    // Accesible para ADMIN o MODERATOR
}
```

### Endpoints de UsuariosController con @PreAuthorize

| Endpoint                       | Método | Autorización                           | Descripción                   |
|--------------------------------|--------|----------------------------------------|-------------------------------|
| `/health`                      | GET    | `hasRole('ADMIN')`                     | Health check solo para admins |
| `/users`                       | GET    | `hasRole('ADMIN')`                     | Listar todos los usuarios     |
| `/users/search`                | GET    | `isAuthenticated()`                    | Buscar usuario por username   |
| `/users/estado`                | GET    | `hasRole('ADMIN')`                     | Filtrar por estado            |
| `/users/notifications-enabled` | GET    | `hasRole('ADMIN')`                     | Usuarios con notificaciones   |
| `/{id}`                        | GET    | `hasRole('ADMIN')`                     | Obtener usuario por ID        |
| `/{id}`                        | PATCH  | `hasRole('ADMIN') or #id == principal` | Editar perfil                 |
| `/{id}`                        | DELETE | `hasRole('ADMIN')`                     | Eliminar usuario              |
| `/{id}/estado`                 | PATCH  | `hasRole('ADMIN')`                     | Cambiar estado                |
| `/{id}/password`               | PUT    | `hasRole('ADMIN') or #id == principal` | Cambiar contraseña            |
| `/{id}/email`                  | PUT    | `hasRole('ADMIN') or #id == principal` | Cambiar email                 |
| `/{id}/discord`                | PUT    | `hasRole('ADMIN') or #id == principal` | Vincular Discord              |
| `/{id}/discord`                | DELETE | `hasRole('ADMIN') or #id == principal` | Desvincular Discord           |

### Endpoints de AuthController con @PreAuthorize

| Endpoint               | Método | Autorización        | Descripción                |
|------------------------|--------|---------------------|----------------------------|
| `/register`            | POST   | **Público**         | Registro de usuarios       |
| `/verify-email`        | POST   | **Público**         | Verificar email            |
| `/resend-verification` | POST   | **Público**         | Reenviar verificación      |
| `/forgot-password`     | POST   | **Público**         | Solicitar restablecimiento |
| `/reset-password`      | POST   | **Público**         | Restablecer contraseña     |
| `/login`               | POST   | **Público**         | Login                      |
| `/refresh`             | POST   | `isAuthenticated()` | Renovar tokens             |
| `/logout`              | POST   | `isAuthenticated()` | Cerrar sesión              |
| `/me`                  | GET    | `isAuthenticated()` | Obtener perfil autenticado |

## Roles en el Sistema

### Enum `Rol` (Domain Layer)

```java
public enum Rol {
    USER,      // Usuario estándar
    ADMIN,     // Administrador (acceso total)
    MODERATOR  // Moderador (futuro uso)
}
```

### Asignación de Roles

- **Registro:** Nuevos usuarios reciben rol `USER` por defecto
- **Cambio de rol:** Solo posible directamente en base de datos (por ahora)
- **Futuro:** Endpoint admin para cambiar roles

## Generación del JWT con Roles

**Ubicación:** `usuarios-service/infrastructure/auth/JwtUtils.java`

```java
public String generateAccessToken(Usuario usuario) {
    return Jwts.builder()
            .subject(usuario.getId().value().toString())
            .claim("username", usuario.getUsername().value())
            .claim("email", usuario.getEmail().value())
            .claim("roles", List.of(usuario.getRole().name())) // ["USER"] o ["ADMIN"]
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
            .id(UUID.randomUUID().toString()) // JTI para revocación
            .signWith(secretKey)
            .compact();
}
```

**Claims del JWT:**

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": [
    "USER"
  ],
  "iat": 1706745600,
  "exp": 1706746500,
  "jti": "abc-123-def-456"
}
```

## Problemas Corregidos

### ❌ Antes (NO FUNCIONABA)

```java
// SecurityConfig sin filtro personalizado
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}

// @PreAuthorize no funcionaba porque:
// - No había Authentication en SecurityContext
// - authentication.principal era null
// - hasRole('ADMIN') siempre fallaba
```

### ✅ Después (FUNCIONA)

```java
// SecurityConfig con GatewayAuthenticationFilter
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}

// GatewayAuthenticationFilter crea Authentication desde headers
// @PreAuthorize ahora funciona correctamente
```

## Testing de Autorización

### Test Manual con cURL

```bash
# 1. Login como USER
curl -X POST http://localhost:8090/v1/usuarios/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123"}'

# Respuesta: { "accessToken": "eyJ...", "refreshToken": "..." }

# 2. Intentar acceder a endpoint ADMIN (debería fallar)
curl http://localhost:8090/v1/usuarios/users \
  -H "Authorization: Bearer eyJ..."

# Respuesta: 403 Forbidden

# 3. Editar propio perfil (debería funcionar)
curl -X PATCH http://localhost:8090/v1/usuarios/{mi-user-id} \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"username":"nuevo_nombre"}'

# Respuesta: 200 OK

# 4. Login como ADMIN
curl -X POST http://localhost:8090/v1/usuarios/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}'

# 5. Acceder a endpoint ADMIN (debería funcionar)
curl http://localhost:8090/v1/usuarios/users \
  -H "Authorization: Bearer eyJ..."

# Respuesta: 200 OK con lista de usuarios
```

### Test de Integración

```java

@SpringBootTest
@AutoConfigureMockMvc
class UsuariosControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/v1/usuarios/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNoPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/v1/usuarios/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user-id-123", roles = "USER")
    void usuarioPuedeEditarSuPropioPerfil() throws Exception {
        mockMvc.perform(patch("/v1/usuarios/user-id-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo_nombre\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user-id-123", roles = "USER")
    void usuarioNoPuedeEditarOtroPerfil() throws Exception {
        mockMvc.perform(patch("/v1/usuarios/otro-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo_nombre\"}"))
                .andExpect(status().isForbidden());
    }
}
```

## Seguridad en Producción

### ⚠️ Consideraciones Importantes

1. **Gateway DEBE ser el único punto de entrada**
    - usuarios-service NO debe ser accesible directamente desde Internet
    - Usar network policies en Kubernetes o security groups en AWS

2. **Validar headers del Gateway**
   ```java
   // En producción, validar que la petición viene del Gateway
   // Opción 1: IP whitelist
   // Opción 2: Secret compartido en header adicional
   ```

3. **jwt.secret DEBE ser idéntico**
    - Gateway y usuarios-service deben usar la misma clave secreta
    - Almacenar en variables de entorno, no en código

4. **Rate limiting en Gateway**
    - Protección contra ataques de fuerza bruta
    - Ya implementado: 100 req/min por IP

5. **Logs de auditoría**
   ```java
   log.info("🔒 AUTHORIZATION_DENIED - User: {} attempted to access: {} - Required role: {}",
     userId, endpoint, requiredRole);
   ```

## Próximos Pasos

- [ ] Implementar tests de integración para cada endpoint protegido
- [ ] Agregar endpoint admin para cambiar roles de usuarios
- [ ] Implementar auditoría de accesos (tabla `audit_log`)
- [ ] Agregar métricas de autorización (endpoints más accedidos, intentos denegados)
- [ ] Documentar en Swagger UI qué rol requiere cada endpoint
- [ ] Configurar perfiles de Spring para desarrollo (sin autenticación) y producción

## Troubleshooting

### Problema: @PreAuthorize no funciona

**Síntomas:** Todos los endpoints devuelven 200 incluso sin el rol correcto

**Solución:**

1. Verificar que `@EnableMethodSecurity` está en `SecurityConfig`
2. Verificar que `GatewayAuthenticationFilter` está registrado
3. Verificar logs: debe aparecer "✅ Authentication creado desde headers del Gateway"

### Problema: 403 Forbidden en endpoints públicos

**Síntomas:** `/auth/login` devuelve 403

**Solución:**

1. Verificar que NO tiene `@PreAuthorize` (endpoints públicos no deben tenerlo)
2. Verificar que el Gateway tiene la ruta en `PUBLIC_PATHS`

### Problema: Usuario puede acceder a recursos de otros

**Síntomas:** User puede editar perfil de otro user

**Solución:**

1. Verificar expresión SpEL: `#id == authentication.principal`
2. El `principal` debe ser el userId (UUID), no username
3. El `#id` debe coincidir exactamente con el `@PathVariable String id`

---

**Última actualización:** 2026-02-04
**Autor:** GameListo Team
