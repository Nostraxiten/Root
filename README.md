<div align="center">

# Root

*Framework modular de cliente Fabric para Minecraft 1.21.5*

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-8.0.0-purple.svg?style=for-the-badge)]()

</div>

## Descripción General

**Root** es un cliente en desarrollo temprano. Cualquier error encontrado podría reportarse dentro del repositorio para soluciones futuras.

> **Nota importante sobre el Repositorio (GitHub):**
> Al subirse a GitHub, el código fuente privado (`RootCode`) no se incluirá en el repositorio remoto.
> El repositorio solo contendrá las siguientes carpetas:
> - **`RootMod`**: Contiene la versión principal, estable y funcional exportada a `.jar` lista para usar.
> - **`OLDvers`**: Contiene versiones antiguas, experimentales o incompletas. Algunas pueden funcionar pero muchas otras pueden ser inestables, tener funciones rotas o estar desactualizadas. Úsalas con precaución.

---
<img width="1877" height="429" alt="Captura de pantalla 2026-07-24 132021" src="https://github.com/user-attachments/assets/eacec5f5-eac3-47ca-919e-b8e7dd4b2690" />
<img width="1919" height="957" alt="Captura de pantalla 2026-07-24 131808" src="https://github.com/user-attachments/assets/2aaa8ac2-62cf-4e6a-95a6-b17ff84d5fc3" />

## Módulos y Opciones

El menú cuenta con una extensa lista de módulos organizados cuidadosamente por categoría. A continuación, se detalla **qué hace exactamente cada opción**.

### Combat (Combate)
*Módulos enfocados en la asistencia durante el combate y el daño.*

| Módulo | Descripción Exacta |
|---|---|
| **AntiKnockback** | Anula o reduce el retroceso al recibir un golpe, evitando que te empujen o te tiren al vacío. |
| **AutoBlock** | Bloquea ataques automáticamente usando tu espada o escudo justo en el momento exacto de recibir daño. |
| **AutoDodge** | Esquiva automáticamente flechas y proyectiles detectando su trayectoria. |
| **AutoTotem** | Equipa instantáneamente un Tótem de Inmortalidad en tu mano secundaria de forma automática cuando tu salud es crítica. |
| **CrystalAura** | Coloca y explota cristales del end automáticamente para hacer daño de área a tus enemigos. Mejorado en 8.0.0: más preciso y fluido. |
| **HitboxExpand** | Expande virtualmente las cajas de colisión (hitboxes) de los enemigos para facilitar enormemente acertar los golpes. |
| **KillAura** | Ataca y golpea automáticamente y a gran velocidad a cualquier entidad o jugador que entre en tu radio de alcance. |
| **Surround** | Coloca bloques automáticamente a tu alrededor para proteger tus pies de explosiones. |
| **TriggerBot** | Golpea automáticamente justo en el milisegundo en el que el punto de mira de tu pantalla se cruza con una entidad. |

**Sub-opciones en el GUI (KillAura / CrystalAura):**

| Opción | Descripción |
|---|---|
| **Engage Range** | Radio en bloques a partir del cual el módulo empieza a atacar/actuar sobre un objetivo (por defecto `8,0`). |
| **Max Engage Distance** | Distancia máxima a la que se sigue considerando "enganchado" a un objetivo antes de soltarlo. |
| **Placement Offset** | Desplazamiento respecto al objetivo al colocar el cristal (CrystalAura). |
| **Crystal Interval (ms)** | Tiempo mínimo en milisegundos entre colocación/detonación de cristales consecutivos. |
| **Anchor Drift Tolerance** | Margen de movimiento permitido del objetivo antes de recalcular el punto de anclaje del cristal. |
| **Airborne Abort Ticks** | Ticks que espera antes de cancelar la acción si el objetivo queda en el aire (evita fallos por salto/knockback). |
| **Approach Timeout (ticks)** | Ticks máximos que intenta acercarse a un objetivo fuera de rango antes de abortar el intento. |
| **Target Players / Target Mobs / Target Animals / Target Hostile** | Checkboxes independientes para filtrar qué tipo de entidad es un objetivo válido. |
| **Auto Move** | Se mueve automáticamente hacia el objetivo si está fuera de alcance. |
| **Move Speed** | Velocidad de ese desplazamiento automático (por defecto `0,3`). |
| **Auto Jump (anti-stuck)** | Salta automáticamente si detecta que se ha quedado atascado contra un bloque. |
| **Throw Pearl On Engage** | Lanza una perla de ender automáticamente al enganchar un objetivo. |
| **Pearl Min Distance** | Distancia mínima al objetivo para que se permita el lanzamiento de la perla. |
| **Rotation Ease** | Suavizado de la rotación de cámara al apuntar (menor = más brusco, mayor = más suave). |
| **Rotation Jitter** | Ruido/aleatoriedad añadido a la rotación para parecer menos robótico. |
| **Smooth Camera** | Activa la interpolación de cámara en general para el módulo. |

**Sub-opciones — Surround:**

| Opción | Descripción |
|---|---|
| **Place Below** | Incluye también el bloque bajo tus pies dentro del anillo de protección, no solo los laterales. |

**Otros módulos con panel de ajustes (⚙) en el GUI**: `TriggerBot`, `AntiKnockback`, `AutoTotem`, `HitboxExpand`, `AutoDodge` — hubo mejoras, investiga las opciones.

### Movement (Movimiento)
*Módulos para alterar la física y desplazamiento del jugador.*

| Módulo | Descripción Exacta |
|---|---|
| **AntiVoid** | Te salva automáticamente de caer al vacío teletransportándote hacia arriba o rebotando para darte la oportunidad de salvarte. |
| **AutoSprint** | Mantiene activado el modo de correr automáticamente siempre que te mueves hacia adelante. |
| **Fly** | Te permite volar libremente por el mundo en modo supervivencia como si estuvieras jugando en modo creativo. |
| **Jesus** | Modifica tu física para permitirte caminar o correr por la superficie del agua o lava sin hundirte jamás. |
| **NoclipTP** | Te permite teletransportarte a través de paredes y bloques sólidos de forma segura usando un fallo de desincronización. |
| **NoFall** | Elimina por completo el daño por caída recibido al saltar o caer desde grandes alturas. |
| **Speed** | Modifica tu fricción y aceleración en el terreno para moverte a velocidades superiores a las del juego base. |
| **Spider** | Te permite trepar cualquier pared vertical de bloques sólidos como si estuvieras subiendo una escalera de mano. |
| **Step** | Aumenta la altura de paso, permitiéndote subir escalones o bloques completos de forma instantánea sin tener que saltar. |
| **OrbitCam** | *(Nuevo en 8.0.0)* Cámara en tercera persona que orbita alrededor de tu jugador sin modificar tu rotación real enviada al servidor. Simula un F5 libre sin ceder control del jugador — compatible con KillAura mientras giras la cámara con el ratón. Preparado para uso con bots. |

**Sub-opciones confirmadas en el GUI:**

| Módulo | Opción | Descripción |
|---|---|---|
| **Speed** | Ground Multiplier | Multiplicador de velocidad mientras estás en el suelo (por defecto `3,0`). |
| **Speed** | Air Multiplier | Multiplicador de velocidad mientras estás en el aire (por defecto `2,0`). |
| **Fly** | Speed | Velocidad de vuelo (por defecto `4,0`). |
| **OrbitCam** | Radius | Distancia orbital de la cámara respecto al jugador (por defecto `12,0`). |
| **OrbitCam** | Sensitivity | Sensibilidad del ratón al orbitar (por defecto `3,0`). |
| **OrbitCam** | Collision | Si está activo, la cámara no atraviesa bloques sólidos al orbitar. |
| **OrbitCam** | Smoothing | Interpolación suave del movimiento orbital en vez de saltos bruscos. |

**Otros módulos con panel de ajustes (⚙) en el GUI**: `Spider`, `Step`, `AntiVoid`, `NoclipTP` — hubo mejoras.

### Player (Jugador)
*Mejoras en la interacción y utilidades del propio jugador.*

| Módulo | Descripción Exacta |
|---|---|
| **AntiAFK** | Realiza micromovimientos y acciones automáticas programadas para evitar que los servidores te expulsen por inactividad. |
| **AutoArmor** | Equipa automáticamente las mejores piezas de armadura que tengas en tu inventario. |
| **AutoEat** | Selecciona la mejor comida de tu inventario y come de manera automática cuando tu nivel de hambre o salud disminuye. |
| **AutoRespawn** | Evita la pantalla de "Has muerto", enviando el paquete de reaparición forzando volver a la vida al instante. |
| **ChestStealer** | Transfiere rápidamente y de forma automática todo el contenido de un cofre a tu inventario nada más abrirlo. |
| **DeathCoords** | Guarda y muestra las coordenadas exactas de tu última muerte en el chat. |
| **HUDOverlay** | Muestra en pantalla información esencial en tiempo real como coordenadas, FPS, lag (TPS), armadura y módulos activos. |
| **NotificationSystem** | Despliega alertas visuales emergentes, elegantes y no intrusivas en pantalla sobre acciones y eventos del cliente. |
| **PanicKey** | Funciona como un botón de emergencia que desactiva de un solo golpe todos los módulos activos para parecer 100% legítimo. |

**Sub-opciones confirmadas en el GUI:**

| Módulo | Opción | Descripción |
|---|---|---|
| **AntiAFK** | Min Interval / Max Interval | Rango de ticks entre cada micromovimiento automático, elegido aleatoriamente dentro de ese rango (por defecto `40`–`100`). |
| **AntiAFK** | Look Noise | Cantidad de ruido aplicado al giro de cámara en cada micromovimiento, para simular un humano (por defecto `1,0`). |
| **AntiAFK** | Strafe Ticks | Duración en ticks de cada movimiento lateral simulado (por defecto `10`). |
| **AutoEat** | Hunger Threshold | Nivel de hambre (sobre 20) por debajo del cual empieza a comer automáticamente (por defecto `15`). |
| **HUDOverlay** | Coordinates / FPS / Biome | Checkboxes independientes para mostrar u ocultar cada dato en el overlay. |
| **DeathCoords** | Auto Save Waypoint | Si está activo, guarda automáticamente la última muerte como waypoint en vez de solo mostrarla por chat. |

**Otros módulos con panel de ajustes (⚙) en el GUI**: `AutoArmor`, `NotificationSystem` — hubo mejoras.
### Render (Visuales)
*Alteraciones gráficas y mejoras visuales de cómo ves el mundo.*

| Módulo | Descripción Exacta |
|---|---|
| **BlockESP** | Resalta bloques de minerales (Diamante, Ancestral, Oro, Hierro, Carbón, Esmeralda, Cobre) a través de las paredes con líneas de colores específicos y radio configurable. |
| **ChestClusters** | Agrupa cofres, cofres trampa y barriles cercanos usando Union-Find. Dibuja una línea hacia el centroide del grupo, una caja envolvente con margen de un bloque y una etiqueta con el conteo. |
| **ESP** | Dibuja cajas 2D/3D (Extra Sensory Perception) precisas alrededor de jugadores y criaturas para revelar sus posiciones fácilmente. |
| **Freecam** | Desprende la cámara de tu cuerpo para explorar los alrededores libremente como espectador, mientras tu personaje físico se queda seguro. |
| **ProjectileTrajectory** | Dibuja una línea que predice exactamente dónde caerán tus flechas o proyectiles antes de lanzarlos. |
| **StorageESP** | Encuentra y dibuja contornos sobre cajas fuertes, cofres, barriles, hornos y shulkers viéndolos a través del suelo o paredes. |
| **Tracers** | Dibuja finas y precisas líneas de colores desde la cruceta central de tu pantalla directamente hacia los jugadores y mobs cercanos. |
| **XRay** | Hace invisibles todos los bloques comunes sin valor revelando instantáneamente solo los minerales valiosos. |
| **Zoom** | Acerca la vista de la cámara con una transición fluida y suave, similar al clásico zoom del mod OptiFine pero configurable. |

**Sub-opciones en el GUI (v8.0.0):**

| Módulo | Opción | Descripción |
|---|---|---|
| **XRay** | Ores / Storage / Fluids / Spawners / FullBright | Checkboxes independientes: qué revelar a través de los bloques (minerales, cofres/barriles, líquidos, generadores) y si forzar brillo total mientras está activo. |
| **ESP** | Players / Hostiles / Animals | Checkboxes para filtrar qué tipo de entidad recibe la caja ESP. |
| **Tracers** | Players / Hostiles | Checkboxes para filtrar a quién se le dibuja la línea desde la cruceta. |
| **Zoom** | Speed | Velocidad de la transición al hacer zoom (por defecto `4,0`). |

**Otros módulos con panel de ajustes (⚙) en el GUI**: `Freecam`, `StorageESP`, `BlockESP`, `ChestClusters` — hubo mejoras, pero no tengo el detalle exacto de sus opciones.

### Optimize (Optimización)
*Mejoras de rendimiento y FPS para el cliente.*

| Módulo | Descripción Exacta |
|---|---|
| **NoFog** | Elimina completamente la niebla de la lejanía, del agua profunda y de la lava, mejorando enormemente la visibilidad. *(Confirmado por el GUI real: vive en Optimize, no en Render como se documentaba antes.)* |
| **NoParticles** | Elimina por completo todas las partículas del juego, ayudando inmensamente a subir los FPS y mejorar el rendimiento. *(Confirmado por el GUI real: vive en Optimize, no en Render como se documentaba antes.)* |

#### FPSBoost
Optimizaciones de renderizado para mejorar los FPS. Es el módulo con más trabajo dedicado en esta versión.

| Opción | Descripción |
|---|---|
| **NoWeather** | Desactiva el renderizado de lluvia y nieve por completo. |
| **NoClouds** | Desactiva el renderizado de nubes. |
| **NoMenuBlur** | Elimina el efecto de desenfoque al abrir menús. |
| **AnimationThrottle** | Reduce la frecuencia de actualización de las animaciones de textura (bloques animados, agua, lava, portales, etc.). |
| **AnimSkipTicks** | Número de ticks que se saltan por cada tick renderizado (0–3). Cuanto mayor, más estática la animación. |
| **StaticAnim** | Congela completamente la animación de texturas (100% estática). Máximo ahorro de CPU en sprites. Tiene prioridad sobre AnimationThrottle. |
| **LowFire** | Reduce el tamaño del overlay de fuego al estar quemándose. |
| **FireScale** | Escala del overlay de fuego (0.1 = mínimo, 1.0 = vanilla). |
| **StaticDrops** | Congela la rotación de los ítems dropeados en el suelo. |
| **LimitEntities** | Activa la distancia de render de entidades personalizada. |
| **EntityDist** | Distancia máxima de render de entidades en bloques. Las entidades más allá no se renderizan. |
| **FastGlint** | Simplifica el efecto glint de encantamiento (una sola pasada en lugar de dos). |

#### ChunkOptimizer
Optimiza la carga y renderizado de chunks.

| Opción | Descripción |
|---|---|
| **MaxRebuilds** | Máximo de secciones de chunk que el juego puede reconstruir por frame (por defecto `15`). Valores más altos cargan chunks más rápido pero pueden causar stuttering. |
| **RebuildDelay** | Retardo en ms entre reconstrucciones de chunk (por defecto `50`). Aumentarlo suaviza el stuttering en CPUs lentas. |
| **LazyChunks** | Optimiza el meshing de chunks fuera de la línea de visión directa. Recomendado mantener activo. |
| **AdaptiveMode** *(confirmado en GUI, no documentado antes)* | Ajusta `MaxRebuilds`/`RebuildDelay` dinámicamente en tiempo real según el FPS actual, en vez de usar valores fijos. |
| **TargetFPS** *(confirmado en GUI, no documentado antes)* | FPS objetivo que `AdaptiveMode` intenta mantener al ajustar los rebuilds (por defecto `240`). |
| **MinRebuilds** *(confirmado en GUI, no documentado antes)* | Suelo mínimo de reconstrucciones por frame que `AdaptiveMode` no bajará aunque el FPS vaya sobrado (por defecto `2`). |

### World (Mundo)
*Automatización y dominio del entorno del juego.*

| Módulo | Descripción Exacta |
|---|---|
| **AutoFish** | Detecta el sonido y movimiento del agua cuando un pez muerde el anzuelo, recoge la caña y vuelve a lanzarla sola. |
| **AutoMine** | Mantiene presionado y activo el botón de romper bloques de forma constante para facilitar túneles sin cansarte de presionar el ratón. |
| **AutoTool** | Analiza en microsegundos el bloque que estás mirando y cambia tu mano de forma automática a la mejor herramienta de tu inventario. |
| **Scaffold** | Coloca mágicamente bloques debajo de tus pies justo a medida que caminas por cornisas o sobre el vacío, tendiendo puentes mientras te mueves. |
| **VillagerClusters** | Implementa un sistema de clustering para agrupar aldeanos cercanos mostrando métricas precisas con líneas y cajas envolventes en 3D. |
| **Chunks** | Resalta chunks visibles diferenciando los de slimes de los normales. |

**Sub-opciones confirmadas — VillagerClusters:**

| Opción | Descripción |
|---|---|
| **scanRadius** | Radio en bloques donde busca aldeanos a agrupar (por defecto `100`). |
| **clusterDistance** | Distancia máxima entre dos aldeanos para considerarlos parte del mismo grupo (por defecto `10`). |
| **minClusterSize** | Número mínimo de aldeanos para que se dibuje un cluster (por defecto `3`). |
| **scanInterval** | Cada cuántos ticks se recalculan los clusters (por defecto `20`). |
| **includeZombie** | Si está activo, incluye también zombie-aldeanos en el conteo/agrupación. |

### Theme (Temas)
*Personalización visual de la interfaz del cliente.*

| Módulo | Descripción Exacta |
|---|---|
| **CustomTheme** | Permite cambiar los colores de la interfaz, textos, fondos y degradados de forma interactiva desde el propio juego. |

**Sub-opciones confirmadas — CustomTheme:**

| Opción | Descripción |
|---|---|
| **Grad Start / Grad End** | Colores de inicio y fin del degradado principal de la interfaz, cada uno con selector HSV completo. |
| **Background** | Color/degradado del fondo de los paneles. |
| **Text** | Color del texto de la interfaz. |
| **Active** | Color usado para resaltar módulos activos/habilitados (el naranja que se ve en Optimize y Player en tu GUI actual). |

---

## Controles de la Interfaz

- **Abrir Menú**: Presiona `Tab + Control` (configurable).
- **Asignar Tecla (Keybind)**: Haz clic derecho en cualquier módulo dentro de la GUI y seguidamente presiona la tecla que desees asignar a ese módulo.
- **Configurar Módulo**: Haz clic en el icono de engranaje o despliega la pestaña de un módulo para ajustar opciones extras precisas (sliders numéricos, opciones de checkbox y menús de modos).
- **Redimensionar Panel** *(nuevo en 8.0.0)*: Arrastra la esquina inferior derecha de cualquier panel de categoría para ajustar su tamaño, con scroll interno automático si el contenido no cabe.

---

## Cómo Compilar y Usar

Para construir tu propio archivo `.jar` y disfrutar del mod a partir del código fuente:

1. **Requisitos**: Java 21 o una versión superior instalada en tu sistema.
2. Abre la terminal o consola de comandos en esta misma carpeta y ejecuta:
   - **En Windows**: `./gradlew.bat build`
   - **En Linux / macOS**: `./gradlew build`
3. Al terminar, el archivo compilado listo para usar aparecerá en la ruta `build/libs/`.
4. Mueve ese archivo `.jar` a tu carpeta local de mods (`%appdata%/.minecraft/mods` en Windows) e inicia el juego asegurándote de usar el perfil cargador de Fabric.

---

> [!WARNING]
> El autor no se responsabiliza de su mal uso en servidores públicos.

---

##  Changelog

### v8.0.0 — 2026-07-25
- **Nuevo Módulo — OrbitCam (Movement)**: Cámara orbital en tercera persona. Sigue la posición del jugador pero permite girar la vista libremente con el ratón sin afectar la rotación real (servidor) del jugador. Compatible con KillAura.
- **ClickGUI rediseñada**: esquinas achaflanadas (chamfer) y bordes con estética "táctica".
- **Paneles redimensionables**: arrastra la esquina inferior derecha de cualquier categoría para ajustar su tamaño, con scroll interno automático.
- **Persistencia de paneles**: posición y tamaño de cada panel se guardan automáticamente entre sesiones.
- **Mejoras al CrystalAura**: más preciso y fluido, con mejores ejemplos de configuración.
- **Nuevo Módulo — VillagerClusters (World)**: agrupa aldeanos cercanos con líneas y cajas envolventes en 3D.
- **Nuevo Módulo — LeavesOptimizer (Optimize)**: convierte hojas en bloques sólidos sin transparencia, color verde uniforme configurable. *(comentado en ModuleManager, ver nota arriba)*
- **Nuevos Módulos de Optimización**: `ChunkOptimizer` y `FPSBoost` para reducir microtirones y desactivar renders costosos.
- **Refactor General**: optimización interna del EventBus.
- PreConfigurado con KillAura y CrystalAura en su mejor configuración.
