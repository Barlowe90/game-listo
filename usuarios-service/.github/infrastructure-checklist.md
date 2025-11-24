# Infrastructure Checklist

## Persistencia

- [ ] Entities en infrastructure/entity.
- [ ] Sin lógica en entidades JPA.
- [ ] MapStruct para conversiones.
- [ ] Repositorios JPA implementan dominios.

## Controladores REST

- [ ] Base path: /v1/usuarios.
- [ ] DTOs en entrada/salida.
- [ ] @Validated + @Valid.
- [ ] Manejo centralizado de errores.

## Seguridad

- [ ] Se extrae userId del JWT.
- [ ] @PreAuthorize donde aplique.

## Configuración

- [ ] Datasource + JPA configurados en application.yml.
