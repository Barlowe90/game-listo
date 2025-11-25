# Guía de Implementación – DDD + Hexagonal

## 🎯 Orden de implementación recomendado

### Fase 1: Dominio (✅ COMPLETADO)

1. ✅ Value Objects con validación
2. ✅ Entidad Usuario con comportamiento
3. ✅ Enums del dominio
4. 📋 Siguiente: Interface RepositorioUsuarios

### Fase 2: Infraestructura - Persistencia (✅ COMPLETADO)

1. ✅ UsuarioEntity (JPA)
2. ✅ UsuarioMapper (Anti-Corruption Layer)
3. 📋 Siguiente: Implementar repositorio JPA

### Fase 3: Infraestructura - Repositorio (📋 PENDIENTE)

```java
// domain/repositories/RepositorioUsuarios.java
public interface RepositorioUsuarios {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UsuarioId id);
    Optional<Usuario> findByEmail(Email email);
    boolean existsByUsername(Username username);
    List<Usuario> searchByUsernameFragment(String fragment);
}

// infrastructure/persistence/postgres/repository/UsuarioRepositorioPostgres.java
@Repository
public class UsuarioRepositorioPostgres implements RepositorioUsuarios {
    
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;
    
    // Implementar métodos usando mapper.toDomain() y mapper.toEntity()
}

// infrastructure/persistence/postgres/repository/UsuarioJpaRepository.java
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM UsuarioEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :fragment, '%'))")
    List<UsuarioEntity> searchByUsernameFragment(@Param("fragment") String fragment);
}
```

### Fase 4: Application - Casos de Uso (📋 PENDIENTE)

```java
// application/IEventosPublisher.java
public interface IEventosPublisher {
    void publishUsuarioCreado(Usuario usuario);
    void publishUsuarioActualizado(Usuario usuario);
    void publishUsuarioEliminado(UsuarioId id);
}

// application/services/ConsultarPerfilPropio.java
@Service
public class ConsultarPerfilPropio {
    
    private final RepositorioUsuarios repositorio;
    
    public ConsultarPerfilPropio(RepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }
    
    public Usuario ejecutar(UsuarioId userId) {
        return repositorio.findById(userId)
            .orElseThrow(() -> new EntidadNoEncontrada("Usuario no encontrado"));
    }
}
```

### Fase 5: Infrastructure - REST API (📋 PENDIENTE)

```java
// infrastructure/api/dto/PerfilUsuarioDTO.java
public record PerfilUsuarioDTO(
    UUID id,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    boolean notificationsActive
) {
    public static PerfilUsuarioDTO fromDomain(Usuario usuario) {
        return new PerfilUsuarioDTO(
            usuario.getId().value(),
            usuario.getUsername().value(),
            usuario.getEmail().value(),
            usuario.getAvatar().isEmpty() ? null : usuario.getAvatar().url(),
            usuario.getRole().name(),
            usuario.getLanguage().name(),
            usuario.isNotificationsActive()
        );
    }
}

// infrastructure/api/rest/UsuarioController.java
@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {
    
    private final ConsultarPerfilPropio consultarPerfil;
    
    @GetMapping("/auth/me")
    public ResponseEntity<PerfilUsuarioDTO> obtenerPerfilPropio(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        UsuarioId userId = UsuarioId.fromString(userDetails.getUsername());
        Usuario usuario = consultarPerfil.ejecutar(userId);
        return ResponseEntity.ok(PerfilUsuarioDTO.fromDomain(usuario));
    }
}
```

### Fase 6: Infrastructure - Manejo de Errores (📋 PENDIENTE)

```java
// infrastructure/exceptions/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntidadNoEncontrada.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntidadNoEncontrada ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidation(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

### Fase 7: Infrastructure - Mensajería (📋 PENDIENTE)

```java
// infrastructure/messaging/publishers/EventosPublisherRabbitMQ.java
@Component
public class EventosPublisherRabbitMQ implements IEventosPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public void publishUsuarioCreado(Usuario usuario) {
        UsuarioCreadoEvent event = new UsuarioCreadoEvent(
            usuario.getId().value(),
            usuario.getEmail().value()
        );
        rabbitTemplate.convertAndSend("usuarios.created", event);
    }
}
```

## 🔑 Reglas clave

### ✅ Dominio

- Constructor privado + factory methods
- Value Objects inmutables
- Validación en constructores
- Comportamiento, no solo getters
- Sin dependencias externas

### ✅ Application

- Un caso de uso = una clase
- Inyecta repositorios e interfaces
- Coordina, no ejecuta lógica
- Publica eventos de dominio

### ✅ Infrastructure

- Adapta dominio a tecnologías
- Mapper traduce entre capas
- Controladores usan DTOs
- No expone entities JPA

## 📝 Convenciones de nombres

### Dominio

- Entidades: `Usuario`
- Value Objects: `UsuarioId`, `Email`, `Username`
- Repositorios: `RepositorioUsuarios` (interface)
- Excepciones: `EntidadNoEncontrada`

### Application

- Casos de uso: `ConsultarPerfilPropio`, `EditarPerfil`
- Commands: `EditarPerfilCommand`
- Queries: `BuscarUsuariosQuery`

### Infrastructure

- Entities: `UsuarioEntity`
- Mappers: `UsuarioMapper`
- Repositorios: `UsuarioRepositorioPostgres`, `UsuarioJpaRepository`
- Controllers: `UsuarioController`
- DTOs: `PerfilUsuarioDTO`, `EditarPerfilRequest`

## 🧪 Testing

```java
// Tests de dominio (sin Spring)
class UsuarioTest {
    @Test
    void deberia_crear_usuario_con_estado_activo() {
        Usuario usuario = Usuario.create(
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("hash123")
        );
        
        assertTrue(usuario.isActive());
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
    }
}

// Tests de caso de uso (con mocks)
@ExtendWith(MockitoExtension.class)
class ConsultarPerfilPropioTest {
    @Mock
    private RepositorioUsuarios repositorio;
    
    @InjectMocks
    private ConsultarPerfilPropio casoDeUso;
    
    @Test
    void deberia_retornar_usuario_cuando_existe() {
        // Given
        UsuarioId id = UsuarioId.generate();
        Usuario usuario = Usuario.create(...);
        when(repositorio.findById(id)).thenReturn(Optional.of(usuario));
        
        // When
        Usuario resultado = casoDeUso.ejecutar(id);
        
        // Then
        assertEquals(usuario, resultado);
    }
}

// Tests de integración (con Spring)
@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void deberia_retornar_perfil_propio() throws Exception {
        mockMvc.perform(get("/v1/usuarios/auth/me")
                .header("Authorization", "Bearer " + validToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").exists());
    }
}
```

## 🚀 Próximos pasos

1. ✅ Completar `RepositorioUsuarios` interface
2. ✅ Implementar `UsuarioRepositorioPostgres`
3. ✅ Crear primer caso de uso: `ConsultarPerfilPropio`
4. ✅ Crear controlador REST básico
5. ✅ Implementar manejo de errores
6. ✅ Añadir más casos de uso según necesidad
7. ✅ Implementar mensajería (opcional al inicio)
