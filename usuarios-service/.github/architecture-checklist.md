# Architecture Checklist – Microservicio Usuarios

## Estructura y Capas

- [ ] La capa `domain` NO depende de Spring ni de ninguna librería externa.
- [ ] La capa `application` depende solo de `domain`.
- [ ] La capa `infrastructure` depende de `domain` y `application`.
- [ ] No hay acceso a la BD desde `application` ni `domain`.
- [ ] No se usan anotaciones JPA fuera de `infrastructure`.
- [ ] No se usan `ResponseEntity` ni `Http*` en `application`.
- [ ] No se exponen entidades JPA al exterior.
- [ ] No se usan `new` para crear entidades fuera del dominio o casos de uso.

## DDD

- [ ] Entidades y Value Objects diferenciados.
- [ ] Value Objects validan invariantes.
- [ ] Repositorios son interfaces en dominio.
- [ ] Reglas de negocio en el dominio.
- [ ] Usuario como raíz de agregado.
- [ ] No colecciones mutables expuestas.

## Flujo

- [ ] Controlador → DTO → Caso de uso → Dominio → Repos → DTO → Controlador.

## Testing

- [ ] Tests unitarios del dominio sin Spring.
- [ ] Casos de uso con mocks.
- [ ] Integración REST + persistencia.
