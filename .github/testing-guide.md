# Guía de Testing – GameListo (DDD + Hexagonal)

## Estrategia de Testing

Esta guía describe los patrones de testing para la arquitectura DDD + Hexagonal del proyecto. Los tests se organizan por capas, manteniendo la independencia del dominio y probando cada componente según su responsabilidad.

## Tipos de Tests

### 1. Tests de Dominio (Unit Tests)

**Objetivo**: Validar lógica de negocio pura sin dependencias externas.

- Sin Spring (`@SpringBootTest`)
- Sin base de datos
- Sin mocks (el dominio es puro)
- Enfoque en Value Objects, entidades y comportamiento

### 2. Tests de Application (Unit Tests con Mocks)

**Objetivo**: Verificar coordinación de casos de uso.

- Mockear repositorios e interfaces
- Verificar flujo de datos
- Validar reglas de negocio en casos de uso

### 3. Tests de Infrastructure (Integration Tests)

**Objetivo**: Probar adaptadores reales (DB, REST, etc.)

- Con `@SpringBootTest`
- Base de datos embebida (H2)
- Verificar mappers y repositorios
- Tests de controladores con MockMvc

---

## 1. Tests de Dominio

### 1.1. Tests de Value Objects

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/domain/usuario/`

**Patrón**: Validar construcción, validaciones y comportamiento.

```java
package com.gamelisto.usuarios_service.domain.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {
    
    @Test
    @DisplayName("Debe crear email válido y normalizarlo a minúsculas")
    void debeCrearEmailValidoYNormalizarlo() {
        // Arrange & Act
        Email email = Email.of("Usuario@Example.COM");
        
        // Assert
        assertEquals("usuario@example.com", email.value());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el email es nulo")
    void debeLanzarExcepcionSiEmailEsNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.of(null)
        );
        
        assertTrue(exception.getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el email es vacío")
    void debeLanzarExcepcionSiEmailEsVacio() {
        assertThrows(IllegalArgumentException.class, () -> Email.of(""));
        assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el formato es inválido")
    void debeLanzarExcepcionSiFormatoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("no-es-email"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("@ejemplo.com"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("usuario@"));
    }
    
    @Test
    @DisplayName("Debe rechazar email que exceda 255 caracteres")
    void debeRechazarEmailDemasiadoLargo() {
        String emailLargo = "a".repeat(250) + "@test.com";
        assertThrows(IllegalArgumentException.class, () -> Email.of(emailLargo));
    }
}
```

**Aplicar mismo patrón para**:

- `UsernameTest.java`
- `PasswordHashTest.java`
- `AvatarTest.java`
- `UsuarioIdTest.java`
- `DiscordUserIdTest.java`
- `DiscordUsernameTest.java`
- `TokenVerificacionTest.java`

### 1.2. Tests de Entidades (Aggregate Root)

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/domain/usuario/`

```java
package com.gamelisto.usuarios_service.domain.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    
    @Test
    @DisplayName("Debe crear nuevo usuario con estado PENDIENTE_DE_VERIFICACION")
    void debeCrearNuevoUsuario() {
        // Arrange
        Username username = Username.of("jugador123");
        Email email = Email.of("jugador@test.com");
        PasswordHash passwordHash = PasswordHash.of("$2a$10$hashed");
        
        // Act
        Usuario usuario = Usuario.create(username, email, passwordHash);
        
        // Assert
        assertNotNull(usuario.getId());
        assertEquals("jugador123", usuario.getUsername().value());
        assertEquals("jugador@test.com", usuario.getEmail().value());
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
        assertEquals(Rol.USER, usuario.getRole());
        assertEquals(Idioma.ESP, usuario.getLanguage());
        assertTrue(usuario.isNotificationsActive());
        assertNotNull(usuario.getCreatedAt());
    }
    
    @Test
    @DisplayName("Debe cambiar username y actualizar timestamp")
    void debeCambiarUsername() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Username nuevoUsername = Username.of("nuevoNombre");
        
        // Act
        usuario.changeUsername(nuevoUsername);
        
        // Assert
        assertEquals("nuevoNombre", usuario.getUsername().value());
        assertTrue(usuario.getUpdatedAt().isAfter(usuario.getCreatedAt()));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al cambiar username a nulo")
    void debeLanzarExcepcionAlCambiarUsernameANulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.changeUsername(null));
    }
    
    @Test
    @DisplayName("Debe cambiar email correctamente")
    void debeCambiarEmail() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Email nuevoEmail = Email.of("nuevo@test.com");
        
        // Act
        usuario.changeEmail(nuevoEmail);
        
        // Assert
        assertEquals("nuevo@test.com", usuario.getEmail().value());
    }
    
    @Test
    @DisplayName("Debe vincular cuenta de Discord")
    void debeVincularCuentaDeDiscord() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.of("123456789");
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        
        // Act
        usuario.linkDiscord(discordId, discordUsername);
        
        // Assert
        assertEquals("123456789", usuario.getDiscordUserId().value());
        assertEquals("player#1234", usuario.getDiscordUsername().value());
        assertNotNull(usuario.getDiscordLinkedAt());
        assertTrue(usuario.isDiscordConsent());
        assertTrue(usuario.hasDiscordLinked());
    }
    
    @Test
    @DisplayName("Debe generar token de verificación al crear usuario")
    void debeGenerarTokenVerificacionAlCrear() {
        // Arrange & Act
        Usuario usuario = Usuario.create(
            Username.of("test"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash")
        );
        
        // Assert
        assertNotNull(usuario.getTokenVerificacion());
        assertFalse(usuario.getTokenVerificacion().isEmpty());
        assertNotNull(usuario.getTokenVerificacionExpiracion());
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
    }
    
    @Test
    @DisplayName("Debe verificar email con token válido")
    void debeVerificarEmailConTokenValido() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        TokenVerificacion token = usuario.getTokenVerificacion();
        
        // Act
        usuario.verificarEmail(token);
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        assertTrue(usuario.getTokenVerificacion().isEmpty());
        assertNull(usuario.getTokenVerificacionExpiracion());
    }
    
    @Test
    @DisplayName("Debe activar usuario tras verificar email")
    void debeActivarUsuarioVerificado() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        TokenVerificacion token = usuario.getTokenVerificacion();
        
        // Act
        usuario.verificarEmail(token);
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    }
    
    // Helper method
    private Usuario crearUsuarioDefault() {
        return Usuario.create(
            Username.of("test"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash")
        );
    }
}
```

---

## 2. Tests de Application (Casos de Uso)

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/application/usecases/`

**Patrón**: Usar mocks de Mockito para repositorios.

```java
package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {
    
    @Mock
    private RepositorioUsuarios repositorioUsuarios;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private CrearUsuarioUseCase crearUsuarioUseCase;
    
    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }
    
    @Test
    @DisplayName("Debe crear usuario exitosamente")
    void debeCrearUsuarioExitosamente() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand(
            "nuevoUsuario",
            "nuevo@test.com",
            "password123"
        );
        
        when(repositorioUsuarios.existsByUsername(any(Username.class)))
            .thenReturn(false);
        when(repositorioUsuarios.existsByEmail(any(Email.class)))
            .thenReturn(false);
        when(passwordEncoder.encode("password123"))
            .thenReturn("$2a$10$hashedPassword");
        when(repositorioUsuarios.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        UsuarioDTO resultado = crearUsuarioUseCase.execute(command);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("nuevoUsuario", resultado.username());
        assertEquals("nuevo@test.com", resultado.email());
        assertEquals("PENDIENTE_DE_VERIFICACION", resultado.status());
        
        verify(repositorioUsuarios).existsByUsername(any(Username.class));
        verify(repositorioUsuarios).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode("password123");
        verify(repositorioUsuarios).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si username ya existe")
    void debeLanzarExcepcionSiUsernameYaExiste() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand(
            "usuarioExistente",
            "nuevo@test.com",
            "password123"
        );
        
        when(repositorioUsuarios.existsByUsername(any(Username.class)))
            .thenReturn(true);
        
        // Act & Assert
        assertThrows(UsernameYaExisteException.class, 
            () -> crearUsuarioUseCase.execute(command));
        
        verify(repositorioUsuarios).existsByUsername(any(Username.class));
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si email ya está registrado")
    void debeLanzarExcepcionSiEmailYaRegistrado() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand(
            "nuevoUsuario",
            "existente@test.com",
            "password123"
        );
        
        when(repositorioUsuarios.existsByUsername(any(Username.class)))
            .thenReturn(false);
        when(repositorioUsuarios.existsByEmail(any(Email.class)))
            .thenReturn(true);
        
        // Act & Assert
        assertThrows(EmailYaRegistradoException.class, 
            () -> crearUsuarioUseCase.execute(command));
        
        verify(repositorioUsuarios, never()).save(any(Usuario.class));
    }
}
```

**Aplicar mismo patrón para**:

- `EditarPerfilUsuarioUseCaseTest.java`
- `ObtenerUsuarioPorIdTest.java`
- `EliminarUsuarioUseCaseTest.java`
- `CambiarEstadoUsuarioUseCaseTest.java`
- `CambiarContrasenaUseCaseTest.java`
- `VerificarEmailUseCaseTest.java`
- `ReenviarVerificacionUseCaseTest.java`
- `RestablecerContrasenaUseCaseTest.java`
- `ObtenerTodosLosUsuariosUseCaseTest.java`

---

## 3. Tests de Infrastructure

### 3.1. Tests de Mappers

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/infrastructure/persistence/postgres/mapper/`

```java
package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.mapper;

import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.persistence.postgres.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {
    
    private final UsuarioMapper mapper = new UsuarioMapper();
    
    @Test
    @DisplayName("Debe convertir Usuario de dominio a UsuarioEntity")
    void debeConvertirDominioAEntity() {
        // Arrange
        Usuario usuario = Usuario.create(
            Username.of("testuser"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash")
        );
        
        // Act
        UsuarioEntity entity = mapper.toEntity(usuario);
        
        // Assert
        assertNotNull(entity);
        assertEquals(usuario.getId().value(), entity.getId());
        assertEquals("testuser", entity.getUsername());
        assertEquals("test@test.com", entity.getEmail());
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, entity.getStatus());
        assertEquals(Rol.USER, entity.getRole());
    }
    
    @Test
    @DisplayName("Debe convertir UsuarioEntity a Usuario de dominio")
    void debeConvertirEntityADominio() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setUsername("testuser");
        entity.setEmail("test@test.com");
        entity.setPasswordHash("$2a$10$hash");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setRole(Rol.USER);
        entity.setLanguage(Idioma.ESP);
        entity.setNotificationsActive(true);
        entity.setStatus(EstadoUsuario.ACTIVO);
        entity.setDiscordConsent(false);
        
        // Act
        Usuario usuario = mapper.toDomain(entity);
        
        // Assert
        assertNotNull(usuario);
        assertEquals(entity.getId(), usuario.getId().value());
        assertEquals("testuser", usuario.getUsername().value());
        assertEquals("test@test.com", usuario.getEmail().value());
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    }
    
    @Test
    @DisplayName("Debe manejar correctamente valores opcionales (Avatar, Discord)")
    void debeManejareValoresOpcionales() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setUsername("testuser");
        entity.setEmail("test@test.com");
        entity.setPasswordHash("$2a$10$hash");
        entity.setAvatar(null);
        entity.setDiscordUserId(null);
        entity.setDiscordUsername(null);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setRole(Rol.USER);
        entity.setLanguage(Idioma.ESP);
        entity.setNotificationsActive(true);
        entity.setStatus(EstadoUsuario.ACTIVO);
        entity.setDiscordConsent(false);
        
        // Act
        Usuario usuario = mapper.toDomain(entity);
        
        // Assert
        assertTrue(usuario.getAvatar().isEmpty());
        assertTrue(usuario.getDiscordUserId().isEmpty());
        assertTrue(usuario.getDiscordUsername().isEmpty());
    }
}
```

### 3.2. Tests de Repositorios (Integration)

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/infrastructure/persistence/postgres/repository/`

```java
package com.gamelisto.usuarios_service.infrastructure.persistence.postgres.repository;

import com.gamelisto.usuarios_service.domain.usuario.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RepositorioUsuariosPostgreTest {
    
    @Autowired
    private RepositorioUsuariosPostgre repositorio;
    
    @Test
    @DisplayName("Debe guardar y recuperar usuario por ID")
    void debeGuardarYRecuperarPorId() {
        // Arrange
        Usuario usuario = crearUsuarioDefault("user1", "user1@test.com");
        
        // Act
        Usuario guardado = repositorio.save(usuario);
        Optional<Usuario> recuperado = repositorio.findById(guardado.getId());
        
        // Assert
        assertTrue(recuperado.isPresent());
        assertEquals(guardado.getId().value(), recuperado.get().getId().value());
        assertEquals("user1", recuperado.get().getUsername().value());
    }
    
    @Test
    @DisplayName("Debe encontrar usuario por email")
    void debeEncontrarPorEmail() {
        // Arrange
        Usuario usuario = crearUsuarioDefault("user2", "user2@test.com");
        repositorio.save(usuario);
        
        // Act
        Optional<Usuario> encontrado = repositorio.findByEmail(
            Email.of("user2@test.com")
        );
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("user2", encontrado.get().getUsername().value());
    }
    
    @Test
    @DisplayName("Debe encontrar usuario por username")
    void debeEncontrarPorUsername() {
        // Arrange
        Usuario usuario = crearUsuarioDefault("user3", "user3@test.com");
        repositorio.save(usuario);
        
        // Act
        Optional<Usuario> encontrado = repositorio.findByUsername(
            Username.of("user3")
        );
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("user3@test.com", encontrado.get().getEmail().value());
    }
    
    @Test
    @DisplayName("Debe verificar si username existe")
    void debeVerificarSiUsernameExiste() {
        // Arrange
        Usuario usuario = crearUsuarioDefault("existente", "existe@test.com");
        repositorio.save(usuario);
        
        // Act
        boolean existe = repositorio.existsByUsername(Username.of("existente"));
        boolean noExiste = repositorio.existsByUsername(Username.of("noexiste"));
        
        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
    }
    
    @Test
    @DisplayName("Debe verificar si email existe")
    void debeVerificarSiEmailExiste() {
        // Arrange
        Usuario usuario = crearUsuarioDefault("user4", "existe@test.com");
        repositorio.save(usuario);
        
        // Act
        boolean existe = repositorio.existsByEmail(Email.of("existe@test.com"));
        boolean noExiste = repositorio.existsByEmail(Email.of("noexiste@test.com"));
        
        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
    }
    
    private Usuario crearUsuarioDefault(String username, String email) {
        return Usuario.create(
            Username.of(username),
            Email.of(email),
            PasswordHash.of("$2a$10$hash")
        );
    }
}
```

### 3.3. Tests de Controladores REST

**Ubicación**: `src/test/java/com/gamelisto/usuarios_service/infrastructure/api/rest/`

```java
package com.gamelisto.usuarios_service.infrastructure.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CrearUsuarioRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuariosControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("GET /v1/usuarios/health debe retornar 200")
    void healthEndpointDebeRetornar200() throws Exception {
        mockMvc.perform(get("/v1/usuarios/health"))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("POST /v1/usuarios/auth/register debe crear usuario")
    void debeCrearUsuario() throws Exception {
        // Arrange
        CrearUsuarioRequest request = new CrearUsuarioRequest(
            "nuevoUsuario",
            "nuevo@test.com",
            "password123"
        );
        
        // Act & Assert
        mockMvc.perform(post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.username", is("nuevoUsuario")))
            .andExpect(jsonPath("$.email", is("nuevo@test.com")))
            .andExpect(jsonPath("$.status", is("PENDIENTE_DE_VERIFICACION")))
            .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    @DisplayName("POST /v1/usuarios/auth/register debe fallar con username duplicado")
    void debeFallarConUsernameDuplicado() throws Exception {
        // Arrange - Crear primer usuario
        CrearUsuarioRequest request1 = new CrearUsuarioRequest(
            "duplicado",
            "user1@test.com",
            "password123"
        );
        mockMvc.perform(post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());
        
        // Act - Intentar crear con mismo username
        CrearUsuarioRequest request2 = new CrearUsuarioRequest(
            "duplicado",
            "user2@test.com",
            "password123"
        );
        
        // Assert
        mockMvc.perform(post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isConflict());
    }
    
    @Test
    @DisplayName("POST /v1/usuarios/auth/register debe validar formato de email")
    void debeValidarFormatoDeEmail() throws Exception {
        // Arrange
        CrearUsuarioRequest request = new CrearUsuarioRequest(
            "usuario",
            "email-invalido",
            "password123"
        );
        
        // Act & Assert
        mockMvc.perform(post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("GET /v1/usuarios/user/{id} debe retornar usuario existente")
    void debeRetornarUsuarioExistente() throws Exception {
        // Arrange - Crear usuario primero
        CrearUsuarioRequest request = new CrearUsuarioRequest(
            "testuser",
            "test@test.com",
            "password123"
        );
        
        String response = mockMvc.perform(post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String userId = objectMapper.readTree(response).get("id").asText();
        
        // Act & Assert
        mockMvc.perform(get("/v1/usuarios/user/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userId)))
            .andExpect(jsonPath("$.username", is("testuser")))
            .andExpect(jsonPath("$.email", is("test@test.com")));
    }
}
```

---

## Comandos Maven

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests de una clase específica
./mvnw test -Dtest=EmailTest

# Ejecutar tests con cobertura
./mvnw test jacoco:report

# Ejecutar solo tests unitarios (sin @SpringBootTest)
./mvnw test -Dgroups="unit"

# Ejecutar solo tests de integración
./mvnw test -Dgroups="integration"
```

---

## Convenciones de Naming

- **Clase**: `[ClaseAProbar]Test.java`
- **Método**: `debe[ComportamientoEsperado]` o `debeLanzarExcepcionSi[Condicion]`
- **DisplayName**: Descripción en español clara del escenario

## Estructura de Tests (AAA Pattern)

```java
@Test
void nombreDelTest() {
    // Arrange (Given) - Preparar datos y mocks
    
    // Act (When) - Ejecutar acción
    
    // Assert (Then) - Verificar resultado
}
```

## Checklist de Testing

- [ ] **Value Objects**: Validar construcción, validaciones y edge cases
- [ ] **Entidades**: Validar factory methods, comportamiento y reglas de negocio
- [ ] **Casos de Uso**: Validar flujo happy path y excepciones con mocks
- [ ] **Mappers**: Validar conversión bidireccional dominio ↔ entity
- [ ] **Repositorios**: Validar operaciones CRUD con H2
- [ ] **Controladores**: Validar endpoints, validaciones y códigos HTTP

## Referencias

- JUnit 5: <https://junit.org/junit5/docs/current/user-guide/>
- Mockito: <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html>
- Spring Boot Testing: <https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing>
