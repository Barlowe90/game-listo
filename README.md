# 🎮 GameListo — Trabajo Fin de Grado (TFG)

> Plataforma social para jugadores basada en microservicios. Este repositorio contiene el código, la
> orquestación y la documentación de un prototipo implementado como Trabajo Fin de Grado.

Autor: Adri R

Contacto: [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/adri-r/)

Resumen ejecutivo
------------------

GameListo es un prototipo de plataforma social para jugadores que permite gestionar bibliotecas de juegos, crear
listas personalizadas, compartir publicaciones y conectar con otros jugadores. El propósito del proyecto es
demostrar patrones arquitectónicos (Domain-Driven Design y Arquitectura Hexagonal) aplicados en una solución de
microservicios construida con Spring Boot 4 y Java 21.

Puntos clave:

- Arquitectura basada en microservicios y separación de responsabilidades.
- Persistencia políglota: PostgreSQL, MongoDB, Neo4j y Redis (cada uno usado según la necesidad del dato).
- BFF GraphQL para agregación de datos y reducción de round-trips hacia el frontend.
- Gateway (Spring Cloud Gateway) que centraliza validación JWT, revocación de tokens y rate-limiting.
- Motor de búsqueda: OpenSearch para búsquedas de texto completo y autocompletado.

Objetivos del proyecto
----------------------

- Diseñar e implementar un prototipo funcional y reproducible que muestre buenas prácticas arquitectónicas.
- Implementar servicios clave: `usuarios`, `catalogo`, `busquedas`, `biblioteca`, `publicaciones` y `social`.
- Proveer una API Gateway y un BFF GraphQL para la capa de frontend.
- Documentar el proceso y facilitar la reproducción mediante Docker Compose.

Resumen de la arquitectura
--------------------------

Topología (resumen):

- `gateway` (Spring Cloud Gateway): validación JWT, revocación (Redis), rate-limiting, header enrichment.
- Microservicios (backend/*): cada dominio es un servicio independiente: `usuarios`, `catalogo`, `busquedas`,
  `biblioteca`, `publicaciones`, `social`.
- `graphql` (BFF): compone respuestas para frontend usando llamadas internas a microservicios.
- Mensajería asíncrona: RabbitMQ (eventos del dominio para reindexado / notificaciones).
- Persistencia:
    - PostgreSQL: datos transaccionales (usuarios, relaciones, bibliotecas mínimas).
    - MongoDB: contenido flexible y enriquecido (detalles de juegos, capturas, posts).
    - Neo4j: grafo social (amistades y recomendaciones basadas en relaciones).
    - OpenSearch: búsqueda y autocompletado.
    - Redis: cache y mecanismo de revocación de tokens.

Decisiones de diseño (resumen)
-----------------------------

- Aplicar KISS: priorizar claridad y simplicidad (es un TFG, no producción).
- Separación clara de capas: `domain` (lógica), `application` (casos de uso), `infrastructure` (adaptadores).
- Gateway valida JWT para centralizar políticas de borde; los microservicios confían en el gateway para
  la validación primaria.
- Uso de Value Objects y patrones DDD en el dominio cuando aporta claridad pedagógica.

Tecnologías destacadas
----------------------

- Java 21, Spring Boot 4 (microservicios)
- Spring Cloud Gateway
- Spring Security (generación de JWT en `usuarios`), BCrypt
- Spring Data (JPA para PostgreSQL, Spring Data MongoDB, OpenSearch client)
- RabbitMQ (Spring AMQP) para mensajería
- Redis (cache y revocación de tokens)
- Neo4j (grafo social)
- OpenSearch (búsqueda)
- Frontend: React + Next.js (aplicación cliente)
- GraphQL (BFF con Spring GraphQL)

Cómo ejecutar (mínimo reproducible)
----------------------------------

Prerequisitos (local development):

- Docker Desktop (Windows)
- PowerShell

Levantar todo (PowerShell):

```powershell
# Desde la raíz del repo
docker compose -f compose.yaml up --build -d

# Para parar y eliminar volúmenes (datos):
docker compose -f compose.yaml down -v
```

Notas para ejecutar un servicio localmente (ejemplo `usuarios`):

```powershell
cd backend\usuarios
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

Comprobaciones rápidas (smoke tests)
----------------------------------

1) Health del gateway:

```powershell
curl http://localhost:8080/actuator/health
```

2) Registro de usuario (ejemplo):

```powershell
curl -X POST http://localhost:8081/v1/usuarios/registro -H "Content-Type: application/json" -d '{"username":"alumno","email":"alumno@example.com","password":"P4ssw0rd"}'
```

3) Login y obtención de tokens:

```powershell
curl -X POST http://localhost:8081/v1/usuarios/auth/login -H "Content-Type: application/json" -d '{"username":"alumno","password":"P4ssw0rd"}'
```

Evidencias y tests
-------------------

- Tests unitarios de dominio: mirar `*/src/test/java` en cada módulo.
- Ejecutar tests por módulo (ejemplo `usuarios`):

```powershell
cd backend\usuarios
.\mvnw.cmd test
```

Estructura del repositorio (resumen)
-----------------------------------

- `backend/` — microservicios (cada uno con su `pom.xml` y `Dockerfile`).
- `frontend/` — Next.js (React) como cliente.
- `compose.yaml`, `compose.ec2.yaml` — orquestación local / EC2.
- `gateway/ARQUITECTURA.md` — documentación detallada del gateway.

Contacto
--------

Si quieres contactarme por motivos profesionales, puedes usar mi perfil de LinkedIn:

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/adri-r/)

Licencia
--------

Este repositorio está disponible bajo la licencia indicada en `LICENSE`.

Más documentación
------------------

- Los README de cada servicio en `backend/*` contienen información específica de cada módulo (endpoints,
  variables de entorno, y notas de ejecución).

---