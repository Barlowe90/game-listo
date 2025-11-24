# Microservicio de Usuarios – GameListo

Microservicio responsable de la **gestión y administración del perfil de usuario** dentro del ecosistema GameListo.  
Se encarga de almacenar, consultar y modificar la información pública y privada del usuario, así como sincronizar cambios procedentes del microservicio Auth.

> **Nota:** Este microservicio *no gestiona autenticación*.  
Auth es quien se encarga del login, verificación de correo, tokens y contraseñas.

---

## 📌 Responsabilidades del microservicio

### ✔️ Funcionalidades principales

- Consulta del perfil propio (`/me`).
- Consulta de un perfil público.
- Edición de datos del perfil:
  - `username`
  - `nombreVisible`
  - `fotoPerfilUrl`
- Sincronización de datos procedentes del microservicio de autenticación:
  - correo electrónico
  - contraseña
  - verificaciones de email
- Gestión del estado del usuario: `ACTIVO`, `SUSPENDIDO`, `ELIMINADO`
- Búsqueda de usuarios por fragmento de texto.

### ❌ Funciones que NO realiza

- Login/Logout
- Verificación de correo
- Recuperación de contraseña
- Emisión de JWT
- Gestión de roles/permisos

---

## 🧱 Arquitectura

El microservicio implementa **DDD + Arquitectura Hexagonal (Ports & Adapters)**.

```
/domain
/application
/infrastructure
/shared
```

---

## 🗃️ Modelo de datos

### Entidad: Usuario

- id (UUID)
- username
- email
- password_hash
- avatar
- creado_el
- actualizado_el
- rol
- idioma
- is_notificaciones_activas
- is_activo
- id_user_discord
- discord_username
- discord_linked_at
- discord_consent

---

## 🔌 Endpoints

Base path: `/v1/usuarios`

- **GET** `/v1/usuarios/health`
- **POST** `/v1/usuarios/auth/login`
- **POST** `/v1/usuarios/auth/logout`
- **POST** `/v1/usuarios/auth/refresh`
- **POST** `/v1/usuarios/auth/register`
- **POST** `/v1/usuarios/auth/verify-email`
- **POST** `/v1/usuarios/auth/forgot-password`
- **POST** `/v1/usuarios/auth/reset-password`
- **GET** `/v1/usuarios/auth/me`
- **POST** `/v1/usuarios/auth/resend-verification`
- **GET** `/v1/usuarios/users`
- **POST** `/v1/usuarios/user/{idUser}/change-password`
- **GET** `/v1/usuarios/user/{idUser}`
- **DELETE** `/v1/usuarios/user/{idUser}`
- **PATCH** `/v1/usuarios/user/{idUser}`
- **PATCH** `/v1/usuarios/user/{idUser}/state`
- **POST** `/v1/usuarios/auth/discord/link/callback`


---

## 🔐 Seguridad

- Requiere JWT (validado en Gateway)
- Un usuario solo puede editar su propio perfil
- Admins pueden suspender cuentas

---

## 🧪 Testing

- Unit tests del dominio
- Tests de casos de uso con mocks
- Tests de integración (REST + JPA)

---

## 🚀 Ejecución local

```
./mvnw spring-boot:run
```

Base de datos:
```
docker compose up -d
```

---

## 📝 Licencia

Proyecto académico del TFG GameListo.
