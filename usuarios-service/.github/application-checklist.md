# Application Checklist – Casos de uso

## Estructura

- [ ] Cada caso de uso en su clase.
- [ ] Inyección por constructor.
- [ ] Sin acceso directo a JPA.
- [ ] Sin errores HTTP.

## DTOs

- [ ] DTOs para entrada y salida.
- [ ] Validación con @Valid.
- [ ] Sin exponer entidades.

## Reglas

- [ ] Servicios de aplicación coordinan, no procesan lógica.
- [ ] No conocen API REST.
- [ ] Uso de Optional o excepciones.

## Excepciones

- [ ] Solo excepciones de dominio.
