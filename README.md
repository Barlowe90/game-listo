# 🎮 GameListo — Plataforma social para jugadores

> *Gestión de videojuegos, listas personalizadas, publicaciones, amigos y seguimiento del progreso.  
Arquitectura basada en microservicios con DDD + Hexagonal Architecture.*

## 🧩 Descripción general

**GameListo** es una plataforma web moderna donde los jugadores pueden **gestionar su biblioteca de videojuegos**, crear **listas personalizadas**, descubrir nuevos títulos y **compartir su experiencia** con una comunidad social.

El proyecto está construido como parte de un Trabajo Fin de Grado en Ingeniería Informática, aplicando principios profesionales de arquitectura de software, despliegue cloud y diseño UI/UX.

## 🚀 Características principales

### 🕹️ Biblioteca del usuario

- Estado del juego: *Lo quiero*, *Lo tengo*, *Jugando*, *Completado*  
- Valoración personal  
- Listas personalizadas (ej. *“Completados 2025”*)

### 🔍 Catálogo

- Juegos obtenidos y enriquecidos desde la API de IGDB  
- Filtros avanzados por género, plataforma, etiquetas, estilo de juego  
- Próximos lanzamientos por mes  
- Top juegos del catálogo

### 👥 Social

- Sistema de amigos  
- Publicaciones
- Grupos de juego y solicitudes  
- Chat y mensajería  

### 🔔 Notificaciones

- Nuevos amigos  
- Invitaciones  

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

| Microservicio | Tecnología | Descripción |
|---------------|------------|-------------|
| **auth-service** | Spring Boot + JWT | Login, logout, gestión de sesiones |
| **usuarios-service** | Spring Boot + PostgreSQL | Registro, verificación email, perfil de usuario, reset contraseña, Discord OAuth2 |
| **catalogo-service** | Spring Boot + PostgreSQL + MongoDB | Juegos, búsqueda y sincronización con IGDB |
| **biblioteca-service** | Spring Boot + PostgreSQL | Estados, listas, reseñas |
| **publicaciones-service** | Spring Boot + MongoDB | Posts, screenshots, vídeos |
| **notificaciones-service** | Spring Boot + MongoDB | Notificaciones del sistema |
| **social-service** | Spring Boot + Neo4j | Amigos, relaciones y recomendaciones |
| **search-service** | Spring Boot + OpenSearch | Buscador y autosuggest |

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
- **Zustand**
- **React Router**
- **GraphQL (Apollo Client)**

## ☁️ Infraestructura y DevOps

- **Docker + Docker Compose**
- **RabbitMQ**
- **OpenSearch**
- **MongoDB / PostgreSQL / Redis / Neo4j**
- **CI/CD con GitHub Actions**
- **Despliegue en AWS (S3, EC2, ECR, RDS, etc.)**

## 👨‍💻 Autor

**Barlowe — Estudiante de ingeniería informática**  
Apasionado por la arquitectura de software, microservicios y desarrollo full‑stack.

## ⭐ Contribuye

Si te gusta el proyecto, ¡déjale una estrella ⭐ en GitHub!
