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

### Fase 4: Application - Casos de Uso (📋 PENDIENTE)

### Fase 5: Infrastructure - REST API (📋 PENDIENTE)

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

## 🚀 Próximos pasos

1. ✅ Completar `RepositorioUsuarios` interface
2. ✅ Implementar `UsuarioRepositorioPostgres`
3. ✅ Crear primer caso de uso: `ConsultarPerfilPropio`
4. ✅ Crear controlador REST básico
5. ✅ Implementar manejo de errores
6. ✅ Añadir más casos de uso según necesidad
7. ✅ Implementar mensajería (opcional al inicio)
