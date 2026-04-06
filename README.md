# 🎮 GameListo — Plataforma social para jugadores

> *Gestión de videojuegos, listas personalizadas, publicaciones y amigos.  
Arquitectura basada en microservicios con DDD + Hexagonal Architecture.*

---

## 🧩 Descripción general

**GameListo** es una plataforma web moderna donde los jugadores pueden **gestionar su biblioteca de videojuegos**, crear
**listas personalizadas**, descubrir nuevos títulos y **buscar compañeros** en una comunidad social.

El proyecto está construido como parte del Trabajo Fin de Grado de Ingeniería Informática, aplicando principios

# 🎮 GameListo — Trabajo Fin de Grado (TFG)

Autor: Adri R

Contacto: [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/adri-r/)

---

Resumen ejecutivo
------------------
GameListo es un prototipo de plataforma social para jugadores construido como Trabajo Fin de Grado. El objetivo del
proyecto es demostrar principios arquitectónicos (DDD y Arquitectura Hexagonal) aplicados en una solución de
microservicios con Spring Boot 4 y Java 21. El prototipo integra persistencia políglota (PostgreSQL, MongoDB, Neo4j,
Redis), búsqueda con OpenSearch y un gateway que centraliza validación JWT y políticas de rate limiting.

Objetivos
---------

- Objetivo general: Diseñar e implementar un prototipo funcional y reproducible que demuestre buenas prácticas de
  arquitectura y patrones de diseño modernos aplicados a una plataforma social para videojuegos.
- Objetivos específicos:
    - Implementar servicios clave: `usuarios`, `catalogo`, `busquedas`, `biblioteca`, `publicaciones` y `social`.
    - Proveer una API Gateway (validación JWT, rate-limiting) y un BFF GraphQL para agregación.
    - Documentar y facilitar la reproducción del entorno mediante Docker Compose y guías para la evaluación.

Alcance y entregables
---------------------
Incluye:

- Código fuente de los microservicios y frontend.
- Scripts y ficheros de orquestación (`compose.yaml`, `compose.ec2.yaml`).
- Documentación técnica básica, guía de ejecución y checklist para el tribunal.

Arquitectura (resumen)
----------------------
Arquitectura basada en microservicios con comunicación REST y eventos (RabbitMQ). Componentes principales:

- `gateway` (Spring Cloud Gateway): validación JWT, revocación mediante Redis, rate-limiting y enrouting.
- Microservicios: `usuarios`, `catalogo`, `biblioteca`, `publicaciones`, `social`, `busquedas` (search service).
- `graphql` (BFF): agrega datos de varios servicios para el frontend.
- Persistencia: PostgreSQL (transaccional), MongoDB (contenido enriquecido), Neo4j (grafo social), OpenSearch (
  búsqueda), Redis (cache & revocación de tokens).

Decisiones de diseño y justificación
-----------------------------------

- Spring Boot 4 + Java 21: compatibilidad con librerías modernas y claridad educativa.
- Gateway valida JWT: centralizar validación para simplificar microservicios internos y evidenciar patrón de borde.
- Políglota solo donde aporta: Postgres para transaccional, MongoDB para multimedia, Neo4j para relaciones.
- Principio KISS: priorizar soluciones simples, legibles y explicables; evitar sobre-ingeniería innecesaria para un TFG.

Cómo ejecutar (mínimo reproducible)
----------------------------------
Requisitos:

- Docker Desktop (Windows)
- PowerShell (Windows) o terminal compatible

Levantar todo (PowerShell):

```powershell
# Desde la raíz del repo
docker compose -f compose.yaml up --build -d

# Para parar y eliminar volúmenes (datos):
docker compose -f compose.yaml down -v
```

Notas para desarrollo de un servicio (ejemplo `usuarios`):

```powershell
cd backend\usuarios
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

Comprobaciones rápidas (smoke tests)
-----------------------------------

1. Health del gateway:

```powershell
curl http://localhost:8080/actuator/health
```

2. Registro de usuario (ejemplo):

```powershell
curl -X POST http://localhost:8081/v1/usuarios/registro -H "Content-Type: application/json" -d 
'{"username":"alumno","email":"alumno@example.com","password":"P4ssw0rd"}'
```

3. Login y obtención de tokens:

```powershell
curl -X POST http://localhost:8081/v1/usuarios/auth/login -H "Content-Type: application/json" -d 
'{"username":"alumno","password":"P4ssw0rd"}'
```

Tests y evidencia
-----------------

- Tests unitarios de dominio: ver `*/src/test/java` en cada módulo.
- Ejecutar tests (por módulo):

```powershell
cd backend\usuarios
.\mvnw.cmd test
```

Estructura del repositorio (resumen)
-----------------------------------

- `backend/` — microservicios (cada uno con su `pom.xml` y `Dockerfile`).
- `frontend/` — Next.js (interfaz cliente).
- `compose.yaml`, `compose.ec2.yaml` — orquestación local / EC2.
- `gateway/ARQUITECTURA.md` — documentación de arquitectura del gateway.

---