# Foundations visuales de GameListo — versión MVP

## Propósito

Este documento define la versión MVP de las foundations visuales y de los componentes atómicos base de GameListo. El objetivo es disponer de una base coherente, reutilizable y mantenible, ajustada al alcance real de un TFG desarrollado por una sola persona.

La prioridad no es cubrir todos los casos de un design system maduro, sino fijar un sistema suficientemente sólido para construir las pantallas principales sin estilos sueltos ni duplicación innecesaria.

La fuente de verdad del sistema son:

- `design-tokens.css`
- `globals.css`
- `tokens.ts`

Los componentes deben consumir esos tokens y evitar valores hardcodeados de color, espaciado, radio, tipografía, sombras o motion, salvo casos muy concretos y justificados.

---

# Fase 4.1 — Foundations visuales reducidas (MVP)

## Objetivo

Definir una base visual global suficientemente consistente para que las pantallas del MVP compartan un mismo lenguaje sin necesidad de resolver estilos caso por caso.

En esta fase se fija el contrato visual del proyecto: color, tipografía, espaciado, radios, elevación y layout base.

## Alcance incluido

### 1. Paleta semántica mínima

El sistema debe trabajar con colores semánticos, no con hexadecimales usados directamente en cada componente.

Tokens principales:

- `primary`
- `secondary`
- `background`
- `surface`
- `card`
- `border`
- `text-primary`
- `text-secondary`
- `text-muted`
- `success`
- `error`

Quedan también disponibles las variantes necesarias para foreground, hover, active y soft cuando ya existan en tokens, pero el MVP no obliga a explotar todas las combinaciones posibles.

### 2. Tipografía mínima

Se fija una tipografía de interfaz común y una escala de tamaños reutilizable.

Se consideran parte del MVP:

- familia principal de interfaz
- familia monoespaciada opcional para datos técnicos
- escala de tamaños principal
- pesos tipográficos básicos
- line-height base

### 3. Escala de espaciado

Toda separación entre elementos debe salir de la escala oficial del sistema.

Escala base:

- 4
- 8
- 12
- 16
- 24
- 32
- 40
- 48
- 64

No deben introducirse espacios arbitrarios salvo necesidad muy concreta.

### 4. Radios

Se fija una escala de radios para que botones, inputs, cards y badges mantengan una misma familia visual.

Escala:

- `sm`
- `md`
- `lg`
- `xl`
- `pill`

### 5. Sombras mínimas

Solo se usan los niveles estrictamente necesarios:

- `surface`
- `elevated`
- `overlay`

### 6. Motion básico

El sistema define duraciones y easing reutilizables para microinteracciones.

En el MVP solo se exige que los componentes base no usen timings arbitrarios y consuman los tokens existentes de duración y easing.

### 7. Layout base

Se consideran foundations del MVP:

- anchuras de contenedor
- gutters principales
- `PageContainer` como primitive oficial de layout

### 8. Accesibilidad mínima razonable

Para un MVP de TFG se mantiene una accesibilidad básica, sin entrar en patrones avanzados.

Se exige:

- contraste razonable
- `focus-visible` visible
- labels correctos en formularios
- tamaño mínimo aceptable de controles interactivos
- `aria-label` en botones de solo icono cuando existan
- no romper el comportamiento nativo de controles HTML

## Alcance aplazado en 4.1

Se aplaza fuera del MVP:

- validación exhaustiva de contraste en todos los escenarios de pantalla
- reglas avanzadas de accesibilidad para componentes complejos
- navegación por teclado avanzada más allá del comportamiento nativo
- patrones ARIA complejos
- documentación excesivamente detallada propia de un design system maduro

## Principios de uso

### Consistencia antes que personalización local

Si un patrón se repite, debe reutilizarse o abstraerse. No deben aparecer estilos inventados para una sola pantalla si ya existe una solución del sistema.

### Semántica antes que valor directo

Se diseña con intención (`primary`, `surface`, `space-4`, `radius-lg`) y no con valores sueltos.

### La home puede ser más expresiva, pero no rompe el sistema

La página pública principal puede usar gradientes, bloques hero y composición más promocional, pero esos recursos no sustituyen los tokens globales ni deben contaminar el resto del producto.

## Entregables de la fase 4.1 MVP

- `design-tokens.css`
- `globals.css`
- `tokens.ts`
- guía breve de foundations en este documento

## Criterio de cierre de la fase 4.1 MVP

La fase 4.1 se considera cerrada cuando:

- colores, spacing, radios y tipografía usados por primitives salen del sistema
- existe una base de layout común
- el foco visible y la interacción base no están rotos
- no hay dependencia estructural de estilos sueltos para construir nuevas pantallas

---

# Fase 4.2 — Componentes atómicos base reducidos (MVP)

## Objetivo

Construir únicamente los componentes atómicos con mayor reutilización real dentro del MVP, evitando patrones complejos que aportan poco valor a corto plazo.

La meta no es tener un catálogo completo de design system, sino disponer de un núcleo pequeño y usable sobre el que montar formularios, navegación principal, tarjetas y feedback básico.

## Componentes incluidos en el MVP

### 1. Button

Componente de acción reutilizable para operaciones principales y secundarias.

Alcance mínimo:

- `primary`
- `secondary`
- `ghost`
- `loading`
- `icon button` solo si realmente se usa en pantallas del MVP

Queda fuera del MVP cualquier expansión de variantes que no tenga uso real inmediato.

### 2. Input

Componente base para formularios y búsqueda.

Alcance mínimo:

- `text`
- `search`
- soporte de `error`
- soporte de `help text`

Puede apoyarse en `Field` como patrón contenedor para label, ayuda y error.

### 3. Badge

Elemento compacto para estados, géneros, categorías o metadatos breves.

Debe basarse en tokens semánticos y ser reutilizable en ficha de juego, publicaciones y filtros.

### 4. Avatar

Componente simple para usuario.

Alcance mínimo:

- imagen
- fallback con iniciales o icono
- tamaños básicos

### 5. Skeleton

Componente de carga visual simple.

Alcance mínimo:

- línea
- bloque/card
- avatar opcional si se usa de verdad

### 6. Toast / feedback

Sistema básico de feedback no bloqueante.

Alcance mínimo:

- `success`
- `error`

Las variantes `warning` e `info` pueden aplazarse si no son necesarias para el MVP.

## Componentes aplazados fuera del MVP

Se aplazan para fases posteriores:

- `Tooltip`
- `Select` custom complejo
- patrones avanzados de feedback
- componentes con interacción rica que exijan mucha accesibilidad específica

Si se necesita selección simple durante el MVP, se prioriza el uso de un `select` nativo estilizado de forma ligera en lugar de construir ahora un componente custom avanzado.

## Qué debe cerrarse en 4.2 MVP

### API consistente de props

Los componentes principales deben compartir convenciones simples y predecibles. Por ejemplo:

- `variant`
- `size`
- `disabled`
- `loading` cuando aplique
- `className`
- `children`

No se busca una API perfecta de librería pública, sino coherencia interna suficiente para el proyecto.

### Variantes visuales mínimas

Cada componente debe tener únicamente las variantes que el MVP use de forma real.

### Tamaños básicos

Debe existir una escala consistente de tamaños en botones, inputs, avatares y elementos similares, sin proliferación de tamaños arbitrarios.

### Integración simple con iconos

Los componentes que usen iconos deben mantener:

- tamaño consistente
- separación razonable respecto al texto
- `aria-label` en botones de solo icono

### Accesibilidad mínima razonable

Para esta fase se exige solo lo imprescindible:

- `focus-visible` visible
- no romper comportamiento nativo
- labels correctos
- estados `disabled` y `loading` comprensibles
- tamaño mínimo aceptable de interacción

No forma parte del MVP implementar navegación por teclado avanzada ni cubrir todos los patrones complejos de accesibilidad.

## Reglas de implementación de 4.2 MVP

1. Ningún componente atómico debe depender de colores, radios, espaciados o timings ajenos al sistema de foundations.
2. No se construyen componentes complejos si existe una alternativa nativa suficiente para el MVP.
3. Las variantes se resuelven desde la API del componente, no duplicando clases en cada pantalla.
4. Solo se implementan componentes con uso real en páginas del MVP.
5. La prioridad es reutilización, velocidad de implementación y consistencia visual.

## Entregables de la fase 4.2 MVP

- `Button`
- `Input`
- `Badge`
- `Avatar`
- `Skeleton`
- `Toast / feedback` básico
- documentación breve del alcance MVP en este archivo

## Criterio de cierre de la fase 4.2 MVP

La fase 4.2 se considera cerrada cuando los componentes atómicos mínimos del MVP pueden reutilizarse en varias pantallas reales del proyecto sin introducir estilos locales inconsistentes, y cuando su apariencia y comportamiento dependen de foundations.

---

# Fase 4.3 — Componentes moleculares e interacción (MVP)

## Objetivo

Construir los primeros patrones compuestos e interactivos que ya aparecen en los mockups del producto, reutilizando los componentes atómicos de la fase 4.2 y manteniendo la coherencia visual definida en foundations.

En esta fase ya no se crean piezas aisladas, sino combinaciones reutilizables con valor directo en pantallas reales, especialmente navegación, ficha de juego, formularios y estados vacíos.

## Componentes incluidos en el MVP

### 1. Dropdown

Patrón desplegable simple para navegación y acciones secundarias.

Uso prioritario en el MVP:

- menú de `Videojuegos` en la navbar
- menús contextuales sencillos si realmente aparecen en pantallas

Alcance MVP:

- apertura y cierre básicos
- cierre al seleccionar opción
- cierre al hacer click fuera cuando sea sencillo de mantener
- consumo de tokens de surface, border, shadow y spacing

Queda fuera del MVP cualquier comportamiento avanzado de menú accesible complejo.

### 2. Tabs

Patrón de navegación local entre bloques de contenido relacionados.

Uso prioritario en el MVP:

- ficha del juego: `Sobre`, `Publicaciones`, `Videos`, `Screenshots`
- reutilización futura en perfil u otras vistas con secciones equivalentes

Alcance MVP:

- listado de tabs
- estado activo visible
- cambio de contenido simple
- variante visual consistente con tokens

No es obligatorio implementar navegación por teclado avanzada entre tabs en esta fase MVP.

### 3. Modal / Dialog

Patrón de capa superpuesta para acciones puntuales que no merecen pantalla completa.

Uso prioritario en el MVP:

- login rápido
- confirmaciones
- añadir a lista
- crear publicación

Alcance MVP:

- header opcional
- body
- footer con acciones cuando aplique
- overlay
- cierre por botón claro y por acción de cancelar

La gestión avanzada de foco puede aplazarse en el MVP, siempre que la interacción no resulte confusa.

### 4. Card

Patrón contenedor reutilizable para representar bloques de información e interacción.

Variantes mínimas del MVP:

- `card base`
- `card clicable`
- `card informativa`
- `card con header/body/footer`

Uso prioritario en el MVP:

- listados de juegos
- publicaciones
- módulos informativos
- bloques destacados de home y ficha

## Patrones adicionales incluidos en 4.3 MVP

### FormField wrapper

Wrapper de formulario para unificar estructura y mensajes.

Debe contemplar:

- `label`
- `input` o `select` nativo
- `help text`
- `error`

Puede apoyarse en el primitive `Field` ya existente y consolidarlo como patrón oficial de formularios del MVP.

### EmptyState

Patrón para vistas sin contenido.

Uso prioritario en el MVP:

- listas vacías
- ausencia de publicaciones
- resultados sin coincidencias
- secciones todavía no pobladas

Debe incluir como mínimo:

- título
- texto breve
- acción opcional
- icono o ilustración simple solo si aporta claridad

### SectionHeader

Patrón de cabecera de sección reutilizable.

Uso prioritario en el MVP:

- bloques de home
- módulos de perfil
- secciones de ficha de juego
- listados con acción secundaria

Debe contemplar como mínimo:

- título
- subtítulo opcional
- acción opcional a la derecha

## Qué debe cerrarse en 4.3 MVP

### Reutilización real en pantallas

Cada componente molecular debe nacer para resolver patrones que ya existen en los mockups o en páginas reales del producto.

### Composición sobre átomos existentes

Dropdown, Tabs, Modal, Card y los patrones auxiliares deben construirse reutilizando Button, Input, Badge, Avatar, Skeleton, Surface y tokens del sistema siempre que sea posible.

### Interacción simple y mantenible

Se prioriza una interacción clara y robusta, sin intentar cubrir todavía todos los edge cases ni todos los comportamientos avanzados de librerías completas.

### Consistencia visual

Los componentes moleculares deben mantener:

- mismos espaciados del sistema
- mismos radios
- mismas sombras
- mismos estados visuales

## Alcance aplazado fuera del MVP en 4.3

Se aplaza para fases posteriores:

- gestión avanzada de foco en modales
- navegación por teclado avanzada en tabs o dropdowns
- animaciones complejas
- variantes excesivas de card
- menús contextuales ricos o altamente configurables

## Entregables de la fase 4.3 MVP

- `Dropdown` simple para navegación
- `Tabs` para ficha del juego
- `Modal / Dialog` básico
- `Card` con variantes mínimas
- `FormField wrapper`
- `EmptyState`
- `SectionHeader`

## Criterio de cierre de la fase 4.3 MVP

La fase 4.3 se considera cerrada cuando los patrones interactivos y compuestos necesarios para las pantallas principales del MVP pueden reutilizarse sin duplicar estructura ni estilos, y cuando su implementación se apoya en los componentes atómicos y foundations ya definidos.

## Fase 4.4 — Layout shell y navegación global

### Objetivo

Construir la estructura estable compartida por la mayoría de páginas del MVP, definiendo una base de navegación y layout consistente para vistas públicas y, cuando aplique, vistas internas.

Esta fase no se centra en componentes aislados, sino en patrones estructurales reutilizables que organizan la interfaz y permiten montar páginas con una jerarquía visual clara.

---

### Alcance MVP de la fase 4.4

En el contexto del MVP, esta fase se limita a los patrones de layout y navegación con uso real e inmediato dentro de la aplicación.

Se priorizan:

- consistencia estructural
- reutilización entre páginas
- adaptación responsive básica
- navegación global clara
- integración con buscador y enlaces principales

Se aplazan patrones avanzados de navegación o layout que no sean necesarios para la primera versión funcional.

---

### Componentes y patrones incluidos

#### AppShell / PublicLayout

Estructura base reutilizable para páginas públicas o compartidas, compuesta por:

- `Header`
- contenido principal
- `Footer`

Debe servir como envoltorio estable para evitar repetir estructura en cada página.

---

#### Header

Cabecera principal del producto.

Debe contemplar como mínimo:

- logo o marca
- navegación principal
- botón de iniciar sesión o acceso
- buscador integrado

Debe mantenerse consistente entre páginas principales del MVP.

---

#### Footer

Pie global con enlaces informativos y legales.

Debe contemplar como mínimo:

- Nosotros
- Contacto
- Política de privacidad
- Legal
- Cookies
- redes o enlaces sociales si se muestran en el mockup final

No requiere una arquitectura compleja; basta con una implementación clara y reutilizable.

---

#### Container

Patrón de anchura controlada para el contenido principal.

Debe consumir los tokens de layout definidos en foundations y evitar anchos arbitrarios en cada página.

---

#### Grid system

Sistema básico de distribución para organizar bloques de contenido.

En el MVP no se busca una malla compleja, sino una convención reutilizable para:

- columnas principales/secundarias
- rejillas de cards
- distribución responsive simple

---

#### Page section wrapper

Contenedor reutilizable para secciones verticales de página.

Debe aportar consistencia en:

- separación entre bloques
- estructura interna de secciones
- ritmo vertical general

---

#### SearchBar

Patrón reutilizable de búsqueda visible en el header y potencialmente reutilizable en otras vistas.

Puede apoyarse en `Input`, `Button` e iconos ya existentes.

---

#### NavLink

Elemento de navegación reutilizable para menús y enlaces de cabecera.

Debe contemplar como mínimo:

- estado normal
- estado activo si aplica
- hover/focus-visible básico
- integración coherente con la navegación principal

---

#### Mobile navigation

Solo se incluye si se implementa en esta fase dentro del MVP.

En caso de abordarse, debe ser una solución simple, funcional y alineada con el `Header`, evitando patrones demasiado complejos.

Si no se implementa ahora, puede dejarse aplazada para una iteración posterior.

---

### Qué debe cerrarse en esta fase

#### 1. Estructura global reutilizable

Las páginas principales no deben repetir manualmente header, footer, container y espaciados estructurales.

Debe existir una base compartida reutilizable.

---

#### 2. Consistencia de navegación

Los enlaces globales y accesos principales deben presentarse de forma uniforme en todas las páginas donde correspondan.

---

#### 3. Responsive básico

La estructura debe adaptarse correctamente entre móvil, tablet y desktop con criterios simples y estables.

No se exige una navegación responsive avanzada, pero sí una adaptación funcional.

---

#### 4. Integración con foundations

Todos los patrones de layout y navegación deben consumir tokens de spacing, colores, radios, sombras y motion ya definidos.

No deben aparecer valores estructurales aislados fuera del sistema.

---

#### 5. Reutilización real

Los patrones creados en esta fase deben utilizarse en páginas reales del proyecto, no quedarse como piezas aisladas sin uso.

---

# Checklist de validación MVP

## Fase 4.1

- [x] Existe una paleta semántica mínima centralizada
- [x] Existe escala de tipografía reutilizable
- [x] Existe escala de espaciado oficial
- [x] Existen radios y sombras oficiales
- [x] El layout base usa contenedores y gutters definidos
- [x] El foco visible base funciona

## Fase 4.2

- [x] Button implementado con variantes mínimas del MVP
- [x] Input implementado con error y ayuda
- [x] Badge implementado
- [x] Avatar implementado
- [x] Skeleton implementado
- [x] Toast básico implementado
- [x] No hay estilos hardcodeados innecesarios fuera del sistema
- [x] Los componentes incluidos tienen uso real en pantallas del MVP

## Fase 4.3

- [x] Dropdown simple implementado para navegación o acciones secundarias
- [x] Tabs implementados para ficha del juego
- [x] Modal / Dialog básico implementado
- [x] Card implementada con variantes mínimas reutilizables
- [x] FormField wrapper unificado
- [x] EmptyState implementado
- [x] SectionHeader implementado
- [x] Los patrones compuestos reutilizan átomos y foundations
- [x] No se duplica estructura ni estilos en las pantallas principales

## Fase 4.4

- [x] AppShell o PublicLayout implementado
- [x] Header implementado con logo, nav, login y buscador
- [x] Footer implementado
- [x] Container reutilizable aplicado en páginas reales
- [x] Grid system básico definido y usado
- [x] Page section wrapper implementado
- [x] SearchBar implementado
- [x] NavLink implementado
- [x] Mobile navigation decidida: implementada o aplazada formalmente
- [x] Layout y navegación consumen tokens del sistema
- [x] No se repite la estructura global manualmente en cada página
