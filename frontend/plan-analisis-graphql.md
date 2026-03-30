# Plan de Análisis: Redundancia de Llamadas vs Copias Reducidas (Refs)

Al aplicar Domain-Driven Design (DDD) con microservicios, el patrón de **Copias Reducidas o Referencias Ligeras (`UserRef`, `GameRef`)** mediante eventos asíncronos (RabbitMQ) está diseñado exactamente para evitar que los servicios se llamen entre sí sincrónicamente o que el frontend tenga que hacer "joins" manuales para datos básicos.

Vamos a analizar página por página para detectar si el Frontend está sobre-consultando o si, por el contrario, cada llamada actual está justificada porque el dominio requiere datos profundos que las "copias reducidas" no poseen.

---

## 1. Patrón Actual de Referencias Ligeras en el Backend

Según la documentación de tus microservicios (README de `biblioteca`, `social`, `publicaciones`), el backend maneja estas copias:

- **`UsuarioRef`**: Contiene `id`, `username`, `avatar`. (Existente en `social`, `biblioteca`, `publicaciones`).
- **`GameRef`**: Contiene `id`, `nombre`, `cover`. (Existente en `biblioteca`, `publicaciones`, `social`).

**Toda página que solo necesite mostrar listas con "Avatar/Nombre" o "Portada/Título" NO DEBE consultar los microservicios dueños (`usuarios` o `catalogo`).**

---

## 2. Análisis por Página

### A. Página: Detalle de Videojuego (`/videojuego/[id]`)

**Llamadas actuales:**

1. `GET /v1/catalogo/games/{id}`
2. `GET /v1/biblioteca/games/{id}/state`
3. `GET /v1/publicaciones/game/{id}`
4. `GET /v1/social/games/{id}/resumen`

**¿Son necesarias todas? SÍ.**
**¿Por qué?**

- `catalogo` **(Obligatorio)**: Es el único que tiene datos extensos del juego (sinopsis, capturas, IGDB metadata, géneros). `GameRef` no tiene esto.
- `biblioteca` **(Obligatorio)**: La copia en el catálogo no sabe si _tú_ estás jugando al juego. Tu estado privado ("Jugando", "Nota: 8/10") solo vive en Biblioteca.
- `publicaciones` **(Obligatorio)**: Solo este dominio sabe qué grupos existen. Su lado positivo es que gracias al `UsuarioRef`, **el frontend no necesita llamar a `usuarios`** para pintar las caras y nombres de los creadores del grupo (se sirven solos).
- `social` **(Obligatorio)**: Solo él conoce el grafo. Al igual que publicaciones, usa `UsuarioRef` para decirte "a tus amigos Jorge y Ana les gusta este juego", sin que iteremos consultando el microservicio `/usuarios`.

**Veredicto REST:** Está perfectamente optimizado en cuanto a evitar llamadas de hidratación básicas, pero como los dominios son inconexos, el Frontend está obligado a invocar a los 4 para tener la "pantalla completa".

---

### B. Página: Perfil de Usuario (`/usuario/[id]`)

**Llamadas actuales:**

1. `GET /v1/usuarios/{id}`
2. `GET /v1/publicaciones/user/{id}`
3. `GET /v1/biblioteca/user/{id}/games`
4. `GET /v1/social/users/friends`

**¿Son necesarias todas? SÍ.**
**¿Por qué?**

- `usuarios` **(Obligatorio)**: Un `UsuarioRef` de otros servicios solo da el avatar y nombre. Pero el perfil necesita datos completos (fecha de registro, biografía, tal vez país, etc.).
- `publicaciones` **(Obligatorio)**: Devuelve la lista de posts de ese usuario. Gracias al `GameRef` en este dominio, **no llamas a `catalogo`** para pintar las portaditas de los juegos de esos posts.
- `biblioteca` **(Obligatorio)**: Devuelve tu estantería completa. También usa `GameRef` internamente.
- `social` **(Obligatorio)**: Para pintar la lista de amigos y el botón de añadir amigo.

**Veredicto REST:** Está funcionando como debe. El frontend hace 4 peticiones paralelas, no se estorban y ninguna busca información que las `Refs` ya hayan solventado.

---

### C. Página: Mi Biblioteca (`/biblioteca`)

**Llamadas actuales:**

1. `GET /v1/biblioteca/lists`
2. `GET /v1/biblioteca/states`

**¿Son necesarias todas? DEPENDERÍA DE TU DISEÑO.**
**¿Por qué?**

- El servicio de `biblioteca` tiene `GameRef`. Esto es genial porque te trae directamente la portada y el nombre de todos tus juegos pendientes/jugados.
- _Posible mejora_: Si ambos endpoints caen bajo el dominio `biblioteca`, podrías plantearte en tu diseño REST (Capa de Aplicación en backend) un único endpoint "Dashboard de Biblioteca" `GET /v1/biblioteca/me/dashboard` que devuelva tanto las listas como los estados de una sentada, con un DTO compuesto.

---

## 3. Conclusión: El verdadero problema y la solución GraphQL

Tu observación sobre las **copias reducidas (GameRef y UsuarioRef)** es **100% correcta y las estás aprovechando**. Si no fuera por ellas, en `/videojuego/[id]` tendrías que haber hecho:
1 llamada a Publicaciones + N llamadas a `/usuarios` por cada participante del grupo.
_Ese es el infierno del n+1 de los microservicios sin duplicación de estado._

Sin embargo, para construir vistas completas, **el Frontend necesita seguir llamando a múltiples microservicios a la vez**. Las _copias reducidas_ evitan llamadas "horizontales y de cruce" de campos básicos (nombres y fotos), pero no evitan llamadas a los grandes dominios (biblioteca, social) para el usuario primario.

### El Rol exacto del GraphQL BFF

Al notar que el Cliente web está haciendo 4 llamadas HTTP desde el navegador (con su respectiva penalización de red por el 3G/4G, latencia TCP, etc):
El BFF (Backend For Frontend) de GraphQL se coloca el medio y le dice a la web:

> "Hazme a mí 1 sola petición POST expresando qué pintas en pantalla, y como yo vivo en la misma red de Kubernetes/Docker que los microservicios, yo hago estas 4 llamadas por ti localmente a 0ms de latencia, ensambo los datos, y te los devuelvo de golpe".

**Plan de Acción BFF recomendado:**

1. Para las páginas públicas simples (`/catalogo`, listar registros), mantenlas con llamadas REST simples o usa GraphQL si lo prefieres para consistencia.
2. **Prioridad Alta para GraphQL**: Refactorizar obligatoriamente `/videojuego/[id]` y `/usuario/[id]`. Deben pasar de invocar a los clientes REST del frontend (`httpClient.get('/v1/...')`) a invocar una única query como:

```graphql
query GetVideojuegoDetalle($id: ID!) {
  juego(id: $id) {
    # Va a Catalogo
    titulo
    descripcion
    portada
  }
  miEstadoBiblioteca(juegoId: $id) {
    # Va a Biblioteca
    estado
    puntuacion
  }
  publicacionesJuego(juegoId: $id) {
    # Va a Publicaciones
    texto
    autor {
      username
      avatar
    } # Usa el UsuarioRef ya guardado
  }
  amigosJugando(juegoId: $id) {
    # Va a Social
    amigos {
      username
      avatar
    }
  }
}
```
