# 🎮 GameListo — Plataforma social para jugadores

> *Gestión de videojuegos, listas personalizadas, publicaciones y amigos.  
Arquitectura basada en microservicios con DDD + Hexagonal Architecture.*

---

## ⚠️ Contexto del Proyecto

**Este es un Trabajo de Fin de Grado (TFG) - NO es un proyecto para producción.**

### 🎯 Filosofía de Desarrollo

Este proyecto sigue el principio **KISS (Keep It Simple, Stupid)**:

- ✅ **Funcionalidad básica y demostrativa** de conceptos arquitectónicos
- ✅ **Código legible y explicable** para la defensa del TFG
- ✅ **Testing mínimo viable** (casos principales, no exhaustivo)
- ❌ **NO sobre-ingeniería** ni optimizaciones prematuras
- ❌ **NO producción** - el enfoque es académico/demostrativo

**Decisiones pragmáticas:**

- Arquitectura hexagonal + DDD correcta pero simplificada
- Persistencia políglota
- Value Objects solo donde mejoran la comprensión del dominio
- Strings/primitivos directos cuando sean suficientes

---

## 🧩 Descripción general

**GameListo** es una plataforma web moderna donde los jugadores pueden **gestionar su biblioteca de videojuegos**, crear
**listas personalizadas**, descubrir nuevos títulos y **buscar compañeros** en una comunidad social.

El proyecto está construido como parte del Trabajo Fin de Grado de Ingeniería Informática, aplicando principios
profesionales de arquitectura de software, despliegue cloud y diseño UI/UX.

## 🚀 Características principales

### 🕹️ Biblioteca del usuario

- Estado del juego: *Lo quiero*, *Lo tengo*, *Jugando*, *Completado*
- Valoración personal
- Listas personalizadas (ej. *“Completados 2025”*)

### 🔍 Catálogo

- Juegos obtenidos y enriquecidos desde la API de IGDB

### 👥 Social

- Sistema de amigos
- Publicaciones
- Grupos de juego y solicitudes

### 🔔 Notificaciones

- Nuevos amigos
- Invitación a grupos de juego

## 🏗️ Arquitectura

GameListo está construido con **microservicios desacoplados** basados en:

### ⚙️ Patrones y principios

- **Domain-Driven Design**
- **Arquitectura Hexagonal**
- **Event-Driven Architecture (RabbitMQ)**
- **Polyglot Persistence**
- **BFF con GraphQL**
- **API Gateway (Spring Cloud Gateway)**

### 🧱 Microservicios

| Microservicio              | Tecnología                         | Puerto | Descripción                                                           |
|----------------------------|------------------------------------|--------|-----------------------------------------------------------------------|
| **api-gateway**            | Spring Cloud Gateway + Redis       | 8090   | Puerta de entrada, validación JWT, rate limiting, CORS                |
| **usuarios-service**       | Spring Boot + PostgreSQL + Redis   | 8081   | Registro, login, perfil, JWT, integrado con Gateway                   |
| **catalogo-service**       | Spring Boot + PostgreSQL + MongoDB | 8082   | Juegos, plataformas, sincronización con IGDB                          |
| **biblioteca-service**     | Spring Boot + PostgreSQL           | 8083   | Estados de juego, listas personalizadas, reseñas                      |
| **publicaciones-service**  | Spring Boot + MongoDB              | 8084   | Posts, screenshots, vídeos                                            |
| **notificaciones-service** | Spring Boot + MongoDB              | 8085   | Notificaciones del sistema                                            |
| **social-service**         | Spring Boot + Neo4j                | 8086   | Grafo social, amistades, relaciones y recomendaciones                 |
| **search-service**         | Spring Boot + OpenSearch           | 8087   | Búsqueda full-text, autocomplete, filtrado facetado                   |
| **graphql-bff**            | Spring GraphQL                     | 8088   | Backend for Frontend, agregación de datos de múltiples microservicios |

### División de Responsabilidades

| Componente           | Responsabilidad                                                   |
|----------------------|-------------------------------------------------------------------|
| **API Gateway**      | Validar JWT (firma, expiración, revocación)                       |
|                      | Rate limiting (100 req/min por IP)                                |
|                      | Agregar headers X-User-*                                          |
|                      | Enrutar a microservicios                                          |
| **usuarios-service** | Generar JWT + Refresh Token                                       |
|                      | CRUD de usuarios                                                  |
|                      | Confiar en headers del Gateway                                    |
|                      | Gestionar refresh tokens en BD                                    |
| **catalogo-service** | Gestionar catálogo de juegos y plataformas                        |
|                      | Sincronizar datos desde IGDB (Scheduler)                          |
|                      | Publicar eventos de dominio (GameCreated, GameUpdated)            |
|                      | Almacenar datos estructurados (PostgreSQL) y multimedia (MongoDB) |
| **search-service**   | Indexar juegos en OpenSearch (escucha eventos)                    |
|                      | Búsqueda full-text, autocomplete                                  |
|                      | Filtrado facetado (plataforma, género, año)                       |
|                      | NO accede directamente a BD (event-driven)                        |
| **graphql-bff**      | Agregar datos de múltiples servicios en una query                 |
|                      | Resolver queries GraphQL llamando APIs REST internas              |
|                      | Reducir round-trips del frontend                                  |
|                      | DataLoader para evitar N+1 queries                                |

### 🔌 Comunicación

- **REST** para consultas simples
- **GraphQL BFF** para agregación de datos
- **Eventos en RabbitMQ** para sincronización eventual

### 🗄️ Persistencia

- PostgreSQL (relacional)
- MongoDB (documentos)
- Neo4j (grafos)
- OpenSearch (búsqueda)
- Redis (caché + mensaje corto)

## 🖥️ Front-End

Diseño moderno creado en **Figma**, inspirado por Discord y Twitch.  
Stack utilizado:

- **React + Vite**
- **TypeScript**
- **TanStack Query**
- **TailwindCSS + ShadCN UI**
- **React Router**
- **GraphQL (Apollo Client)**

## ☁️ Infraestructura y DevOps

- **Docker + Docker Compose**
- **RabbitMQ**
- **Redis**
- **PostgreSQL**
- **MongoDB / OpenSearch / Neo4j**
- **CI/CD con GitHub Actions**
- **Despliegue en AWS (S3, EC2, ECR, RDS, etc.)**

## 🐳 Inicio Rápido con Docker Compose

### Requisitos Previos

- Docker Desktop instalado y ejecutándose
- Git
- Terminal

### Levantar Todo el Sistema

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/game-listo.git
cd game-listo

# 2. (Opcional) Configurar variables de entorno
cp .env.example .env
# Editar .env y cambiar JWT_SECRET

# 3. Levantar todos los servicios
docker-compose up -d
```

## 👨‍💻 Autor

**Barlowe — Estudiante de ingeniería informática**