# Domain Checklist – Microservicio Usuarios

## Modelado

- [ ] Usuario con ID inmutable.
- [ ] Email y Username como VOs.
- [ ] Value Objects sin Lombok.
- [ ] Validación estricta de VOs.

## Invariantes

- [ ] Email válido obligatorio.
- [ ] Username válido.
- [ ] Fecha de registro no nula.
- [ ] Estado por defecto: ACTIVA.

## Repositorios

- [ ] Interfaces sin anotaciones.
- [ ] save(), findById(), findByEmail(), existsByUsername(), searchByUsernameFragment().

## Código prohibido

- [ ] Sin Spring.
- [ ] Sin JPA.
- [ ] Sin DTOs.
- [ ] Sin logs.
