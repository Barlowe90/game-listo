# Tests de Application Layer (Casos de Uso) ✅

## Resumen de Implementación

Se han creado tests unitarios completos para los **Casos de Uso** de la capa de aplicación, utilizando **Mockito** para simular dependencias y validar la coordinación de la lógica de negocio.

## Tests Implementados

### ✅ CrearUsuarioUseCaseTest.java (9 tests)

**Funcionalidad Principal:**

- ✅ Crear usuario exitosamente
- ✅ Hashear contraseña antes de guardar

**Validaciones de Negocio:**

- ✅ Lanzar excepción si username ya existe
- ✅ Lanzar excepción si email ya está registrado
- ✅ Validar formato de email antes de verificar existencia
- ✅ Validar formato de username antes de verificar existencia

**Comportamiento:**

- ✅ Crear usuario con valores por defecto correctos
- ✅ Normalizar email a minúsculas
- ✅ Verificar existencia con valores normalizados

### ✅ ObtenerUsuarioPorIdTest.java (6 tests)

**Funcionalidad Principal:**

- ✅ Obtener usuario por ID exitosamente
- ✅ Retornar DTO con todos los campos del usuario

**Validaciones:**

- ✅ Lanzar excepción si usuario no existe
- ✅ Lanzar excepción si ID tiene formato inválido

**Comportamiento:**

- ✅ Convertir correctamente ID de string a UsuarioId
- ✅ Manejar UUID con mayúsculas y minúsculas

### ✅ EditarPerfilUsuarioUseCaseTest.java (11 tests)

**Funcionalidad Principal:**

- ✅ Editar avatar del usuario
- ✅ Editar idioma del usuario
- ✅ Habilitar/deshabilitar notificaciones del usuario
- ✅ Editar múltiples campos a la vez

**Validaciones:**

- ✅ Ignorar campos nulos sin modificar el usuario
- ✅ Lanzar excepción si usuario no existe
- ✅ Lanzar excepción si ID tiene formato inválido
- ✅ Validar URL del avatar (máximo 500 caracteres)
- ✅ Lanzar excepción si idioma es inválido

**Comportamiento:**

- ✅ Actualizar timestamp al editar perfil

## Patrones de Testing con Mockito

### Uso de Mocks

```java
@Mock
private RepositorioUsuarios repositorioUsuarios;

@Mock
private PasswordEncoder passwordEncoder;

@InjectMocks
private CrearUsuarioUseCase crearUsuarioUseCase;
```

### Configuración de Comportamiento

```java
when(repositorioUsuarios.existsByUsername(any(Username.class)))
    .thenReturn(false);
    
when(repositorioUsuarios.save(any(Usuario.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));
```

### Verificación de Interacciones

```java
verify(repositorioUsuarios).save(any(Usuario.class));
verify(passwordEncoder).encode("password123");
verify(repositorioUsuarios, never()).save(any());
```

### Verificación con Argumentos Específicos

```java
verify(repositorioUsuarios).save(argThat(usuario -> 
    usuario.getPasswordHash().value().equals(hashedPassword)
));
```

## Resultados de Ejecución

```text
╔═══════════════════════════════════════════════════════════════╗
║                    TESTS TOTALES DEL PROYECTO                 ║
╠═══════════════════════════════════════════════════════════════╣
║  DOMAIN LAYER                                                 ║
║    EmailTest                    │  13 tests  │  ✅ PASSING    ║
║    UsernameTest                 │  13 tests  │  ✅ PASSING    ║
║    PasswordHashTest             │   7 tests  │  ✅ PASSING    ║
║    UsuarioIdTest                │  12 tests  │  ✅ PASSING    ║
║    UsuarioTest                  │  34 tests  │  ✅ PASSING    ║
║                                 │  79 tests  │                ║
╠═══════════════════════════════════════════════════════════════╣
║  APPLICATION LAYER                                            ║
║    CrearUsuarioUseCaseTest      │   9 tests  │  ✅ PASSING    ║
║    ObtenerUsuarioPorIdTest      │   6 tests  │  ✅ PASSING    ║
║    EditarPerfilUsuarioUseCase   │  11 tests  │  ✅ PASSING    ║
║                                 │  26 tests  │                ║
╠═══════════════════════════════════════════════════════════════╣
║  OTROS                                                        ║
║    UsuariosServiceApplication   │   1 test   │  ✅ PASSING    ║
╠═══════════════════════════════════════════════════════════════╣
║  TOTAL                          │ 106 tests  │  ✅ SUCCESS    ║
╚═══════════════════════════════════════════════════════════════╝
```

## Comandos para Ejecutar

```bash
# Ejecutar todos los tests de Application Layer
./mvnw test -Dtest="com.gamelisto.usuarios_service.application.usecases.*Test"

# Ejecutar tests de un caso de uso específico
./mvnw test -Dtest=CrearUsuarioUseCaseTest

# Ejecutar todos los tests del proyecto
./mvnw test
```

## Características de los Tests

### ✅ Patrones Aplicados

- **@ExtendWith(MockitoExtension.class)**: Integración con JUnit 5
- **@Mock**: Simulación de dependencias
- **@InjectMocks**: Inyección automática de mocks
- **AAA Pattern**: Arrange-Act-Assert
- **Verificación de interacciones**: `verify()` para asegurar llamadas correctas
- **Argumentos específicos**: `argThat()` para validaciones complejas
- **Never invocations**: `never()` para verificar que algo NO se llamó

### ✅ Validaciones Cubiertas

- **Happy path**: Flujos exitosos de creación, consulta y edición
- **Excepciones de negocio**: Username duplicado, email duplicado, usuario no encontrado
- **Validaciones de formato**: UUID inválido, email inválido, username inválido
- **Comportamiento de mocks**: Verificación de llamadas y orden de ejecución
- **Edge cases**: Campos nulos, múltiples campos, normalización de datos

### ✅ Cobertura de Casos de Uso

- ✅ **CrearUsuarioUseCase** - Creación de nuevos usuarios
- ✅ **ObtenerUsuarioPorId** - Consulta de usuarios
- ✅ **EditarPerfilUsuarioUseCase** - Edición de perfiles
- ⏳ **EliminarUsuarioUseCase** - Pendiente
- ⏳ **ObtenerTodosLosUsuariosUseCase** - Pendiente

## Próximos Pasos

1. ✅ **Value Objects** - COMPLETADO (45 tests)
2. ✅ **Tests de Usuario.java** - COMPLETADO (34 tests)
3. ✅ **Tests de Casos de Uso** - COMPLETADO (26 tests)
4. 🔄 **Tests de Mappers** (Anti-Corruption Layer) - Siguiente
5. ⏳ Tests de Repositorios (Integration)
6. ⏳ Tests de Controladores REST (Integration)

## Notas Técnicas

- **Framework**: JUnit 5 (Jupiter) + Mockito
- **Java Version**: 21
- **Build Tool**: Maven
- **Tiempo de ejecución**: ~1.4 segundos (Application Layer)
- **Mockito**: Simulación de repositorios y servicios externos
- **No Spring**: Tests unitarios puros sin contexto de Spring

## Referencias

- Guía de Testing: `.github/testing-guide.md`
- Copilot Instructions: `.github/copilot-instructions.md`
- Mockito Docs: <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html>
- JUnit 5 Docs: <https://junit.org/junit5/docs/current/user-guide/>
