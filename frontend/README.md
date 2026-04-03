# GameListo — Frontend

Frontend de GameListo construido con React y Next.js, diseñado para ofrecer alto rendimiento, buena indexabilidad (SEO) y una experiencia de desarrollo ágil.

Tabla de contenidos

- Resumen
- Características
- Tecnologías clave
- Quickstart
- Variables de entorno
- Arquitectura de renderizado
- Estilos y accesibilidad
- Contribuir
- Referencias

## Resumen

Aplicación Next.js que consume el BFF (GraphQL) a través del API Gateway. El proyecto prioriza SSR/ISR para páginas públicas de alto tráfico, combinando rendimiento, frescura de contenido y eficiencia operativa.

## Características

- Renderizado híbrido: Server-Side Rendering (SSR) y Incremental Static Regeneration (ISR).
- Estilos con Tailwind CSS y componentes headless para patrones accesibles.
- Internacionalización con React-Intl.
- Chat delegando a la API de Discord.

## Tecnologías clave

- Next.js — enrutado por archivos, SSR/ISR.
- React — UI.
- Tailwind CSS — utility-first styling.
- Headless components — accesibilidad sin dependencias de estilo.
- React-Intl — i18n y formatos locales.

## Quickstart

Requisitos: Node.js (>=16 LTS recomendado), pnpm/npm/yarn.

Instalar dependencias:

```bash
pnpm install
# o
npm install
```

Modo desarrollo:

```bash
pnpm dev
# o
npm run dev
```

Construir y ejecutar producción local:

```bash
pnpm build
pnpm start
```

## Variables de entorno

Archivo recomendado: `.env` en la raiz del repositorio (`game-listo/.env`).

Variables habituales (ejemplos):

- `NEXT_PUBLIC_API_URL` — URL pública del API Gateway o BFF (accesible desde el navegador).
- `NEXT_PUBLIC_API_GRAPHQL_URL` — Endpoint GraphQL público (opcional, por defecto `${NEXT_PUBLIC_API_URL}/graphql`).
- `API_URL_INTERNAL` — URL interna para SSR/RSC en Docker (ejemplo: `http://gateway:8080`).
- `API_GRAPHQL_INTERNAL_URL` — Endpoint GraphQL interno para SSR/RSC en Docker.

## Arquitectura de renderizado

- Páginas públicas de alto tráfico: ISR o SSR según la necesidad.
- Revalidación bajo demanda: el back-end puede disparar revalidaciones cuando publica eventos relevantes, manteniendo la información actualizada sin saturar microservicios.
- Todas las peticiones cliente pasan por Spring Cloud Gateway, heredando autenticación, CORS y rate-limiting.

## Estilos y accesibilidad

- Tailwind CSS para un CSS compacto y predecible.
- Componentes headless para diálogos, menús y tooltips, garantizando control visual y mejores bundles.
