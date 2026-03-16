# Implementacion explicada del sistema visual MVP de GameListo

## Para que sirve este documento

Este texto explica, de forma clara y sin lenguaje demasiado tecnico, como se ha montado la base visual de GameListo y por que esta organizada asi.

La idea principal es simple:

en lugar de disenar cada pantalla desde cero, el proyecto tiene una base comun para que todo se vea parte del mismo producto.

Gracias a eso:

- los colores se repiten con sentido
- los botones y formularios se comportan de forma parecida
- el espaciado entre bloques mantiene una logica comun
- la navegacion y los bloques de contenido reutilizan la misma estructura
- cambiar o mejorar el sistema mas adelante es mucho mas facil

---

## La idea general

La implementacion se ha hecho en cuatro capas:

### 1. Una base comun para todo el proyecto

Aqui se decide el aspecto general del producto:

- colores
- tipografia
- espaciados
- bordes redondeados
- sombras
- foco visible
- contenedores de pagina

### 2. Un grupo pequeno de componentes atomicos reutilizables

Encima de esa base se construyeron primero los componentes mas pequenos y repetidos del MVP:

- boton
- input
- badge
- avatar
- skeleton
- feedback tipo toast

### 3. Un grupo pequeno de patrones compuestos

Despues, sobre esos atomos, se construyeron patrones mas completos para pantallas reales:

- dropdown
- tabs
- dialog
- card
- form field wrapper
- empty state
- section header

### 4. Un layout shell global reutilizable

Cuando la base, los atomos y los patrones compuestos ya estuvieron estables, se cerro una capa estructural comun para casi toda la aplicacion:

- `AppShell`
- `Header`
- `Footer`
- `Container`
- `Grid`
- `PageSection`
- `SearchBar`
- `NavLink`

La ventaja de este enfoque es que una pantalla nueva no necesita inventarse su propio estilo ni su propia estructura. Solo tiene que usar la base y los componentes que ya existen.

---

## Donde esta cada parte

### Base visual global

- `frontend/src/styles/design-tokens.css`
  Aqui viven las decisiones visuales principales del proyecto. Es la lista central de colores, tamanos de letra, espaciados, radios, sombras y medidas base.

- `frontend/src/styles/globals.css`
  Aqui se aplica esa base a toda la aplicacion. Por ejemplo, el foco visible, el comportamiento general de botones e inputs, y varias utilidades comunes.

- `frontend/src/theme/tokens.ts`
  Es la misma base visual, pero preparada para poder consultarla desde TypeScript si hace falta.

### Componentes atomicos

- `frontend/src/shared/components/ui/Button.tsx`
- `frontend/src/shared/components/ui/Input.tsx`
- `frontend/src/shared/components/ui/Field.tsx`
- `frontend/src/shared/components/ui/Badge.tsx`
- `frontend/src/shared/components/ui/Avatar.tsx`
- `frontend/src/shared/components/ui/Skeleton.tsx`
- `frontend/src/shared/components/ui/Toast.tsx`

### Componentes moleculares y patrones compuestos

- `frontend/src/shared/components/ui/Dropdown.tsx`
- `frontend/src/shared/components/ui/Tabs.tsx`
- `frontend/src/shared/components/ui/Dialog.tsx`
- `frontend/src/shared/components/ui/Card.tsx`
- `frontend/src/shared/components/ui/FormField.tsx`
- `frontend/src/shared/components/ui/EmptyState.tsx`
- `frontend/src/shared/components/ui/SectionHeader.tsx`

### Layout comun

- `frontend/src/shared/components/layout/AppShell.tsx`
- `frontend/src/shared/components/layout/Header.tsx`
- `frontend/src/shared/components/layout/Footer.tsx`
- `frontend/src/shared/components/layout/Container.tsx`
- `frontend/src/shared/components/layout/Grid.tsx`
- `frontend/src/shared/components/layout/PageSection.tsx`
- `frontend/src/shared/components/layout/SearchBar.tsx`
- `frontend/src/shared/components/layout/NavLink.tsx`
- `frontend/src/shared/components/ui/PageContainer.tsx`
  `PageContainer` sigue existiendo como primitive oficial, pero ahora se apoya en `Container` para no duplicar la logica de anchura y gutters.

### Componentes de apoyo donde ya se aprovechan

- `frontend/src/shared/components/ui/PlaceholderPage.tsx`
- `frontend/src/app/(public)/layout.tsx`
- `frontend/src/app/(auth)/layout.tsx`
- `frontend/src/app/(private)/layout.tsx`

---

## Como se ha implementado, explicado de forma sencilla

### 1. Primero se fijo una base visual unica

Antes de construir componentes, se definio una base comun para todo:

- que colores representan acciones principales, secundarias, exito o error
- que separaciones se pueden usar entre elementos
- que tamanos de letra forman la escala principal
- que redondeados y sombras se consideran oficiales
- como debe verse el foco cuando un elemento recibe atencion

Esto evita que cada pantalla tome decisiones distintas por su cuenta.

### 2. Despues se creo el contenedor comun de pagina

Con `PageContainer` se resolvio un problema muy frecuente:

si cada pagina define su anchura y sus margenes a mano, el producto acaba viendose irregular.

Por eso ahora existe un contenedor comun que marca el ancho y el aire lateral de las pantallas.

### 3. Luego se construyeron los componentes mas repetidos

### Button

El boton se ha dejado con lo minimo necesario para el MVP:

- accion principal
- accion secundaria
- accion discreta
- estado de carga

La idea aqui no es tener mil versiones, sino cubrir los casos reales del producto con una API pequena y facil de entender.

### Input y Field

El input se usa para formularios y tambien queda listo para busquedas simples.

`Field` acompana al input para resolver de manera clara:

- la etiqueta
- el texto de ayuda
- el mensaje de error

Asi los formularios no tienen que rehacerse desde cero cada vez.

### Badge

Se usa para pequenos fragmentos de informacion, como etiquetas cortas o estados simples.

Se ha dejado reducido a las variantes realmente necesarias en el MVP.

### Avatar

El avatar muestra la imagen del usuario si existe.

Si no existe, muestra unas iniciales o un icono sencillo. De esta forma el producto no depende de que siempre haya foto.

### Skeleton

El skeleton es la version visual de "todavia estoy cargando".

En lugar de ensenar un espacio vacio o un simple texto, ensena una forma parecida al contenido que va a aparecer.

### Toast / feedback

El feedback se ha resuelto con un componente muy simple para mostrar mensajes de exito o error sin bloquear la pantalla.

En este MVP no se ha construido un sistema complejo. La idea era cubrir bien lo importante con una solucion ligera.

### 4. Despues se construyeron patrones compuestos e interactivos

Cuando la base y los atomos ya estuvieron estables, el siguiente paso fue dejar de pensar en piezas aisladas y empezar a resolver pantallas reales.

La idea aqui fue simple:

no crear componentes porque si, sino patrones que ya hacian falta en la navbar, en la ficha del juego, en formularios y en estados vacios.

### Dropdown

Se creo un dropdown sencillo para navegacion.

Su primer uso real esta en el menu de `Videojuegos` de la navbar.

No intenta cubrir todos los casos avanzados de una libreria completa. Solo resuelve bien lo necesario para el MVP:

- abrir y cerrar
- mostrar opciones de navegacion
- cerrarse al elegir una opcion
- mantener el mismo lenguaje visual del sistema

### Tabs

Las tabs se anadieron para dividir contenido relacionado sin convertir la ficha del juego en una pagina larga y desordenada.

En la ficha demo ya separan:

- `Sobre`
- `Publicaciones`
- `Videos`
- `Screenshots`

Asi cada bloque tiene su sitio sin tener que rehacer estructura ni estilos.

### Dialog

El dialog se implemento para acciones puntuales que necesitan una capa por encima de la pagina, pero no una ruta nueva.

En la ficha del juego ya se usa para `Anadir a mi lista`.

Con eso el MVP ya tiene un patron claro para:

- acciones rapidas
- confirmaciones simples
- pequenos formularios dentro de una capa superpuesta

### Card

La card se convirtio en el contenedor reutilizable principal de esta fase.

Se dejo preparada para varios usos reales:

- card base
- card clicable
- card informativa
- card con header, body y footer

Esto evita que home, catalogo, biblioteca o ficha tengan cada una su propia caja inventada.

### FormField wrapper

`FormField` no sustituye a `Field`, sino que lo consolida como patron oficial de formulario del MVP.

Su utilidad es que deja mas clara la intencion del sistema:

- etiqueta
- control
- ayuda
- error

Y ademas sirve tanto para `Input` como para un `select` nativo sencillo.

### EmptyState

`EmptyState` se anadio para que las vistas sin contenido no queden resueltas con texto suelto.

Ahora las listas vacias, secciones sin videos o zonas todavia no pobladas pueden usar el mismo patron visual:

- titulo
- descripcion breve
- accion opcional
- icono simple

### SectionHeader

`SectionHeader` se creo para no repetir una y otra vez el mismo bloque de:

- titulo
- subtitulo
- accion a la derecha

Es pequeno, pero ahorra mucha duplicacion en home, catalogo, biblioteca y ficha del juego.

### 5. Despues se cerro la shell global de layout y navegacion

Cuando los atoms y los patrones compuestos ya eran utiles, seguia existiendo un problema:

las paginas todavia podian repetir por su cuenta la cabecera, el pie, los espaciados estructurales y parte de la navegacion.

Para cerrar eso, en la fase 4.4 se construyo una capa de layout estable para casi toda la aplicacion.

La idea no fue crear "mas componentes" porque si.

La idea fue fijar una estructura comun para que las paginas importantes del MVP ya arranquen desde una base compartida.

### AppShell

`AppShell` se convirtio en el envoltorio principal de las zonas publicas, auth y privadas.

Su trabajo es simple:

- pintar siempre el mismo `Header`
- dejar un espacio claro para el contenido principal
- cerrar la pagina con el mismo `Footer`

Con eso, el proyecto deja de decidir cabecera y pie pantalla por pantalla.

### Header

El header ya no es solo una barra visual.

Ahora resuelve una parte real de la navegacion global del producto:

- marca de GameListo
- acceso a `Videojuegos`
- enlaces principales
- buscador visible
- acceso a login o estado de usuario

Ademas, en movil se dejo una navegacion simple con apertura y cierre claros, suficiente para el MVP sin entrar en un patron mas complejo del necesario.

### Footer

El footer se implemento como la pieza que cierra la shell global y da salida a enlaces informativos.

No intenta ser una arquitectura compleja.

Su valor en esta fase es mas practico:

- no repetir bloques legales o informativos
- mantener consistencia al final de cada vista
- confirmar que la app ya tiene una estructura mas cercana a un producto real

### Container, PageSection y Grid

Aqui es donde la fase 4.4 se vuelve especialmente util para construir pantallas nuevas.

`Container` fija anchuras y gutters comunes.

`PageSection` fija el ritmo vertical entre bloques de pagina.

`Grid` fija varias distribuciones reutilizables para cards, contenido principal con lateral, resumenes y otras composiciones sencillas del MVP.

La ventaja de esto es que una pagina no tiene que inventarse su layout desde cero.

Ya existe una forma comun de decidir:

- cuanto aire hay entre bloques
- como se reparten las columnas
- como se comporta el contenido entre movil y desktop

### SearchBar y NavLink

Estas dos piezas cierran la parte mas visible de la fase 4.4.

`SearchBar` deja de ser un input suelto y pasa a ser un patron reutilizable de busqueda.

Ahora puede vivir en el header, en el catalogo o en otras vistas sin cambiar su lenguaje visual.

`NavLink` hace lo mismo con los enlaces de navegacion:

- mismo estilo base
- mismo tratamiento de hover y foco
- mismo estado activo

Eso evita que cada menu tenga que resolverse con clases distintas o logicas repetidas.

---

## Donde se ve ya en la aplicacion

### Formularios de login, registro y recuperacion

Estas pantallas ya no son placeholders.

Ahora usan el mismo patron compartido:

- `AppShell`
- `PageContainer`
- `Card`
- `SectionHeader`
- `FormField`
- `Input`
- `Button`
- `Toast`

Eso las convierte en una buena referencia para futuros formularios del producto.

### Header y Footer

La shell global usa la base comun y muestra tambien estados reales:

- `AppShell` compartido para rutas publicas, auth y privadas
- botones y enlaces con el mismo lenguaje visual
- `Dropdown` para `Videojuegos`
- `SearchBar` conectado con `/catalogo?q=...`
- `Avatar` cuando hay usuario
- `Skeleton` mientras la sesion esta cargando
- footer con enlaces informativos y legales reales

### Home y catalogo

Estas pantallas ya no dependen solo de un placeholder general.

Ahora reutilizan:

- `AppShell`
- `PageSection`
- `Grid`
- `SearchBar`
- `SectionHeader`
- `Card`
- `Badge`
- cards clicables para navegar

Eso empieza a darles una composicion mas real sin romper el sistema.

### Ficha del videojuego

La ficha demo es la mejor muestra de como 4.3 y 4.4 ya trabajan juntas.

En una sola pantalla ya se estan usando:

- `PageSection`
- `Grid`
- `SectionHeader`
- `Card`
- `Tabs`
- `Dialog`
- `FormField`
- `EmptyState`
- `Badge`
- `Button`

Eso la convierte en la referencia principal para futuros detalles de juego.

### Biblioteca y otras vistas sin contenido

La biblioteca ahora usa `PageSection`, `Grid`, `Card` y `EmptyState` para el caso sin juegos guardados.

Eso hace que un estado vacio se vea como parte del producto y no como una pantalla a medio hacer.

Ademas, al vivir ya dentro de la shell privada, no necesita reconstruir header, footer o navegacion global.

### PlaceholderPage y vistas provisionales

`PlaceholderPage` tambien se actualizo para apoyarse mejor en la capa de layout shell.

Las vistas provisionales siguen siendo simples, pero ahora heredan mejor la estructura comun mediante `PageSection`, `Card`, `SectionHeader` y `Button`.

---

## Que significa esto para la persona que continue el proyecto

Si alguien tiene que seguir construyendo GameListo, la regla practica es esta:

### Si va a crear una pantalla nueva

No deberia empezar poniendo estilos sueltos.

Lo normal es:

1. dejar que la ruta herede `AppShell` desde su layout cuando corresponda
2. usar `PageSection` o `PageContainer` para arrancar con una estructura estable
3. reutilizar `Grid` si la vista necesita columnas o rejillas repetibles
4. reutilizar `SectionHeader` si la pantalla necesita una cabecera clara
5. reutilizar `Card` si hace falta un bloque visual
6. usar `FormField`, `Input`, `Button`, `Badge`, `Avatar`, `Skeleton` o `Toast` si encajan
7. usar `Dropdown`, `Tabs`, `Dialog`, `EmptyState`, `SearchBar` o `NavLink` si el patron ya existe
8. tirar de la base comun antes de inventar colores o tamanos nuevos

### Si necesita un estilo nuevo

Primero deberia preguntarse:

- esto ya existe en el sistema?
- se parece a algo que ya hace otra pantalla?
- realmente hace falta una variante nueva?

Si la respuesta es que el patron se va a repetir, entonces tiene sentido ampliar el sistema.

Si solo es una necesidad puntual, hay que tener cuidado para no romper la coherencia visual.

---

## Resumen corto

La implementacion no busca impresionar por complejidad.

Busca algo mas util para un MVP:

- una base visual comun
- unos atomos y patrones compuestos bien elegidos
- una shell global que ordena la navegacion y el layout
- una forma clara de construir pantallas sin rehacer decisiones una y otra vez

Dicho de la manera mas simple posible:

GameListo ya no se pinta pantalla a pantalla.
Ahora se construye sobre una base comun, unos atomos compartidos, unos patrones compuestos reutilizables y una shell global que da estructura a casi todo el MVP.
