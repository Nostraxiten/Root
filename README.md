<div align="center">

# NoxMenu

*Framework modular de cliente Fabric para Minecraft 1.21.5*

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-8.0.0-purple.svg?style=for-the-badge)]()

</div>

## DescripciÃ³n General

**NoxMenu** es un cliente en desarrollo temprano. Cualquier error encontrado podrÃ­a reportarse dentro del repositorio para soluciones futuras.

> **Nota importante sobre el Repositorio (GitHub):**
> Al subirse a GitHub, el cÃ³digo fuente privado (`NoxMenuCode`) no se incluirÃ¡ en el repositorio remoto.
> El repositorio solo contendrÃ¡ las siguientes carpetas:
> - **`RootMod`**: Contiene la versiÃ³n principal, estable y funcional exportada a `.jar` lista para usar.
> - **`OLDvers`**: Contiene versiones antiguas, experimentales o incompletas. Algunas pueden funcionar pero muchas otras pueden ser inestables, tener funciones rotas o estar desactualizadas. Ãšsalas con precauciÃ³n.

---
<img width="1877" height="429" alt="Captura de pantalla 2026-07-24 132021" src="https://github.com/user-attachments/assets/eacec5f5-eac3-47ca-919e-b8e7dd4b2690" />
<img width="1919" height="957" alt="Captura de pantalla 2026-07-24 131808" src="https://github.com/user-attachments/assets/2aaa8ac2-62cf-4e6a-95a6-b17ff84d5fc3" />

## MÃ³dulos y Opciones

El menÃº cuenta con una extensa lista de mÃ³dulos organizados cuidadosamente por categorÃ­a. A continuaciÃ³n, se detalla **quÃ© hace exactamente cada opciÃ³n**.

### Combat (Combate)
*MÃ³dulos enfocados en la asistencia durante el combate y el daÃ±o.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **AntiKnockback** | Anula o reduce el retroceso al recibir un golpe, evitando que te empujen o te tiren al vacÃ­o. |
| **AutoBlock** | Bloquea ataques automÃ¡ticamente usando tu espada o escudo justo en el momento exacto de recibir daÃ±o. |
| **AutoDodge** | Esquiva automÃ¡ticamente flechas y proyectiles detectando su trayectoria. |
| **AutoTotem** | Equipa instantÃ¡neamente un TÃ³tem de Inmortalidad en tu mano secundaria de forma automÃ¡tica cuando tu salud es crÃ­tica. |
| **CrystalAura** | Coloca y explota cristales del end automÃ¡ticamente para hacer daÃ±o de Ã¡rea a tus enemigos. Mejorado en 8.0.0: mÃ¡s preciso y fluido. |
| **HitboxExpand** | Expande virtualmente las cajas de colisiÃ³n (hitboxes) de los enemigos para facilitar enormemente acertar los golpes. |
| **KillAura** | Ataca y golpea automÃ¡ticamente y a gran velocidad a cualquier entidad o jugador que entre en tu radio de alcance. |
| **Surround** | Coloca bloques automÃ¡ticamente a tu alrededor para proteger tus pies de explosiones. |
| **TriggerBot** | Golpea automÃ¡ticamente justo en el milisegundo en el que el punto de mira de tu pantalla se cruza con una entidad. |

**Sub-opciones en el GUI (KillAura / CrystalAura):**

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **Engage Range** | Radio en bloques a partir del cual el mÃ³dulo empieza a atacar/actuar sobre un objetivo (por defecto `8,0`). |
| **Max Engage Distance** | Distancia mÃ¡xima a la que se sigue considerando "enganchado" a un objetivo antes de soltarlo. |
| **Placement Offset** | Desplazamiento respecto al objetivo al colocar el cristal (CrystalAura). |
| **Crystal Interval (ms)** | Tiempo mÃ­nimo en milisegundos entre colocaciÃ³n/detonaciÃ³n de cristales consecutivos. |
| **Anchor Drift Tolerance** | Margen de movimiento permitido del objetivo antes de recalcular el punto de anclaje del cristal. |
| **Airborne Abort Ticks** | Ticks que espera antes de cancelar la acciÃ³n si el objetivo queda en el aire (evita fallos por salto/knockback). |
| **Approach Timeout (ticks)** | Ticks mÃ¡ximos que intenta acercarse a un objetivo fuera de rango antes de abortar el intento. |
| **Target Players / Target Mobs / Target Animals / Target Hostile** | Checkboxes independientes para filtrar quÃ© tipo de entidad es un objetivo vÃ¡lido. |
| **Auto Move** | Se mueve automÃ¡ticamente hacia el objetivo si estÃ¡ fuera de alcance. |
| **Move Speed** | Velocidad de ese desplazamiento automÃ¡tico (por defecto `0,3`). |
| **Auto Jump (anti-stuck)** | Salta automÃ¡ticamente si detecta que se ha quedado atascado contra un bloque. |
| **Throw Pearl On Engage** | Lanza una perla de ender automÃ¡ticamente al enganchar un objetivo. |
| **Pearl Min Distance** | Distancia mÃ­nima al objetivo para que se permita el lanzamiento de la perla. |
| **Rotation Ease** | Suavizado de la rotaciÃ³n de cÃ¡mara al apuntar (menor = mÃ¡s brusco, mayor = mÃ¡s suave). |
| **Rotation Jitter** | Ruido/aleatoriedad aÃ±adido a la rotaciÃ³n para parecer menos robÃ³tico. |
| **Smooth Camera** | Activa la interpolaciÃ³n de cÃ¡mara en general para el mÃ³dulo. |

**Sub-opciones â€” Surround:**

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **Place Below** | Incluye tambiÃ©n el bloque bajo tus pies dentro del anillo de protecciÃ³n, no solo los laterales. |

**Otros mÃ³dulos con panel de ajustes (âš™) en el GUI**: `TriggerBot`, `AntiKnockback`, `AutoTotem`, `HitboxExpand`, `AutoDodge` â€” hubo mejoras, investiga las opciones.

### Movement (Movimiento)
*MÃ³dulos para alterar la fÃ­sica y desplazamiento del jugador.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **AntiVoid** | Te salva automÃ¡ticamente de caer al vacÃ­o teletransportÃ¡ndote hacia arriba o rebotando para darte la oportunidad de salvarte. |
| **AutoSprint** | Mantiene activado el modo de correr automÃ¡ticamente siempre que te mueves hacia adelante. |
| **Fly** | Te permite volar libremente por el mundo en modo supervivencia como si estuvieras jugando en modo creativo. |
| **Jesus** | Modifica tu fÃ­sica para permitirte caminar o correr por la superficie del agua o lava sin hundirte jamÃ¡s. |
| **NoclipTP** | Te permite teletransportarte a travÃ©s de paredes y bloques sÃ³lidos de forma segura usando un fallo de desincronizaciÃ³n. |
| **NoFall** | Elimina por completo el daÃ±o por caÃ­da recibido al saltar o caer desde grandes alturas. |
| **Speed** | Modifica tu fricciÃ³n y aceleraciÃ³n en el terreno para moverte a velocidades superiores a las del juego base. |
| **Spider** | Te permite trepar cualquier pared vertical de bloques sÃ³lidos como si estuvieras subiendo una escalera de mano. |
| **Step** | Aumenta la altura de paso, permitiÃ©ndote subir escalones o bloques completos de forma instantÃ¡nea sin tener que saltar. |
| **OrbitCam** | *(Nuevo en 8.0.0)* CÃ¡mara en tercera persona que orbita alrededor de tu jugador sin modificar tu rotaciÃ³n real enviada al servidor. Simula un F5 libre sin ceder control del jugador â€” compatible con KillAura mientras giras la cÃ¡mara con el ratÃ³n. Preparado para uso con bots. |

**Sub-opciones confirmadas en el GUI:**

| MÃ³dulo | OpciÃ³n | DescripciÃ³n |
|---|---|---|
| **Speed** | Ground Multiplier | Multiplicador de velocidad mientras estÃ¡s en el suelo (por defecto `3,0`). |
| **Speed** | Air Multiplier | Multiplicador de velocidad mientras estÃ¡s en el aire (por defecto `2,0`). |
| **Fly** | Speed | Velocidad de vuelo (por defecto `4,0`). |
| **OrbitCam** | Radius | Distancia orbital de la cÃ¡mara respecto al jugador (por defecto `12,0`). |
| **OrbitCam** | Sensitivity | Sensibilidad del ratÃ³n al orbitar (por defecto `3,0`). |
| **OrbitCam** | Collision | Si estÃ¡ activo, la cÃ¡mara no atraviesa bloques sÃ³lidos al orbitar. |
| **OrbitCam** | Smoothing | InterpolaciÃ³n suave del movimiento orbital en vez de saltos bruscos. |

**Otros mÃ³dulos con panel de ajustes (âš™) en el GUI**: `Spider`, `Step`, `AntiVoid`, `NoclipTP` â€” hubo mejoras.

### Player (Jugador)
*Mejoras en la interacciÃ³n y utilidades del propio jugador.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **AntiAFK** | Realiza micromovimientos y acciones automÃ¡ticas programadas para evitar que los servidores te expulsen por inactividad. |
| **AutoArmor** | Equipa automÃ¡ticamente las mejores piezas de armadura que tengas en tu inventario. |
| **AutoEat** | Selecciona la mejor comida de tu inventario y come de manera automÃ¡tica cuando tu nivel de hambre o salud disminuye. |
| **AutoRespawn** | Evita la pantalla de "Has muerto", enviando el paquete de reapariciÃ³n forzando volver a la vida al instante. |
| **ChestStealer** | Transfiere rÃ¡pidamente y de forma automÃ¡tica todo el contenido de un cofre a tu inventario nada mÃ¡s abrirlo. |
| **DeathCoords** | Guarda y muestra las coordenadas exactas de tu Ãºltima muerte en el chat. |
| **HUDOverlay** | Muestra en pantalla informaciÃ³n esencial en tiempo real como coordenadas, FPS, lag (TPS), armadura y mÃ³dulos activos. |
| **NotificationSystem** | Despliega alertas visuales emergentes, elegantes y no intrusivas en pantalla sobre acciones y eventos del cliente. |
| **PanicKey** | Funciona como un botÃ³n de emergencia que desactiva de un solo golpe todos los mÃ³dulos activos para parecer 100% legÃ­timo. |

**Sub-opciones confirmadas en el GUI:**

| MÃ³dulo | OpciÃ³n | DescripciÃ³n |
|---|---|---|
| **AntiAFK** | Min Interval / Max Interval | Rango de ticks entre cada micromovimiento automÃ¡tico, elegido aleatoriamente dentro de ese rango (por defecto `40`â€“`100`). |
| **AntiAFK** | Look Noise | Cantidad de ruido aplicado al giro de cÃ¡mara en cada micromovimiento, para simular un humano (por defecto `1,0`). |
| **AntiAFK** | Strafe Ticks | DuraciÃ³n en ticks de cada movimiento lateral simulado (por defecto `10`). |
| **AutoEat** | Hunger Threshold | Nivel de hambre (sobre 20) por debajo del cual empieza a comer automÃ¡ticamente (por defecto `15`). |
| **HUDOverlay** | Coordinates / FPS / Biome | Checkboxes independientes para mostrar u ocultar cada dato en el overlay. |
| **DeathCoords** | Auto Save Waypoint | Si estÃ¡ activo, guarda automÃ¡ticamente la Ãºltima muerte como waypoint en vez de solo mostrarla por chat. |

**Otros mÃ³dulos con panel de ajustes (âš™) en el GUI**: `AutoArmor`, `NotificationSystem` â€” hubo mejoras.
### Render (Visuales)
*Alteraciones grÃ¡ficas y mejoras visuales de cÃ³mo ves el mundo.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **BlockESP** | Resalta bloques de minerales (Diamante, Ancestral, Oro, Hierro, CarbÃ³n, Esmeralda, Cobre) a travÃ©s de las paredes con lÃ­neas de colores especÃ­ficos y radio configurable. |
| **ChestClusters** | Agrupa cofres, cofres trampa y barriles cercanos usando Union-Find. Dibuja una lÃ­nea hacia el centroide del grupo, una caja envolvente con margen de un bloque y una etiqueta con el conteo. |
| **ESP** | Dibuja cajas 2D/3D (Extra Sensory Perception) precisas alrededor de jugadores y criaturas para revelar sus posiciones fÃ¡cilmente. |
| **Freecam** | Desprende la cÃ¡mara de tu cuerpo para explorar los alrededores libremente como espectador, mientras tu personaje fÃ­sico se queda seguro. |
| **ProjectileTrajectory** | Dibuja una lÃ­nea que predice exactamente dÃ³nde caerÃ¡n tus flechas o proyectiles antes de lanzarlos. |
| **StorageESP** | Encuentra y dibuja contornos sobre cajas fuertes, cofres, barriles, hornos y shulkers viÃ©ndolos a travÃ©s del suelo o paredes. |
| **Tracers** | Dibuja finas y precisas lÃ­neas de colores desde la cruceta central de tu pantalla directamente hacia los jugadores y mobs cercanos. |
| **XRay** | Hace invisibles todos los bloques comunes sin valor revelando instantÃ¡neamente solo los minerales valiosos. |
| **Zoom** | Acerca la vista de la cÃ¡mara con una transiciÃ³n fluida y suave, similar al clÃ¡sico zoom del mod OptiFine pero configurable. |

**Sub-opciones en el GUI (v8.0.0):**

| MÃ³dulo | OpciÃ³n | DescripciÃ³n |
|---|---|---|
| **XRay** | Ores / Storage / Fluids / Spawners / FullBright | Checkboxes independientes: quÃ© revelar a travÃ©s de los bloques (minerales, cofres/barriles, lÃ­quidos, generadores) y si forzar brillo total mientras estÃ¡ activo. |
| **ESP** | Players / Hostiles / Animals | Checkboxes para filtrar quÃ© tipo de entidad recibe la caja ESP. |
| **Tracers** | Players / Hostiles | Checkboxes para filtrar a quiÃ©n se le dibuja la lÃ­nea desde la cruceta. |
| **Zoom** | Speed | Velocidad de la transiciÃ³n al hacer zoom (por defecto `4,0`). |

**Otros mÃ³dulos con panel de ajustes (âš™) en el GUI**: `Freecam`, `StorageESP`, `BlockESP`, `ChestClusters` â€” hubo mejoras, pero no tengo el detalle exacto de sus opciones.

### Optimize (OptimizaciÃ³n)
*Mejoras de rendimiento y FPS para el cliente.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **NoFog** | Elimina completamente la niebla de la lejanÃ­a, del agua profunda y de la lava, mejorando enormemente la visibilidad. *(Confirmado por el GUI real: vive en Optimize, no en Render como se documentaba antes.)* |
| **NoParticles** | Elimina por completo todas las partÃ­culas del juego, ayudando inmensamente a subir los FPS y mejorar el rendimiento. *(Confirmado por el GUI real: vive en Optimize, no en Render como se documentaba antes.)* |

#### FPSBoost
Optimizaciones de renderizado para mejorar los FPS. Es el mÃ³dulo con mÃ¡s trabajo dedicado en esta versiÃ³n.

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **NoWeather** | Desactiva el renderizado de lluvia y nieve por completo. |
| **NoClouds** | Desactiva el renderizado de nubes. |
| **NoMenuBlur** | Elimina el efecto de desenfoque al abrir menÃºs. |
| **AnimationThrottle** | Reduce la frecuencia de actualizaciÃ³n de las animaciones de textura (bloques animados, agua, lava, portales, etc.). |
| **AnimSkipTicks** | NÃºmero de ticks que se saltan por cada tick renderizado (0â€“3). Cuanto mayor, mÃ¡s estÃ¡tica la animaciÃ³n. |
| **StaticAnim** | Congela completamente la animaciÃ³n de texturas (100% estÃ¡tica). MÃ¡ximo ahorro de CPU en sprites. Tiene prioridad sobre AnimationThrottle. |
| **LowFire** | Reduce el tamaÃ±o del overlay de fuego al estar quemÃ¡ndose. |
| **FireScale** | Escala del overlay de fuego (0.1 = mÃ­nimo, 1.0 = vanilla). |
| **StaticDrops** | Congela la rotaciÃ³n de los Ã­tems dropeados en el suelo. |
| **LimitEntities** | Activa la distancia de render de entidades personalizada. |
| **EntityDist** | Distancia mÃ¡xima de render de entidades en bloques. Las entidades mÃ¡s allÃ¡ no se renderizan. |
| **FastGlint** | Simplifica el efecto glint de encantamiento (una sola pasada en lugar de dos). |

#### ChunkOptimizer
Optimiza la carga y renderizado de chunks.

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **MaxRebuilds** | MÃ¡ximo de secciones de chunk que el juego puede reconstruir por frame (por defecto `15`). Valores mÃ¡s altos cargan chunks mÃ¡s rÃ¡pido pero pueden causar stuttering. |
| **RebuildDelay** | Retardo en ms entre reconstrucciones de chunk (por defecto `50`). Aumentarlo suaviza el stuttering en CPUs lentas. |
| **LazyChunks** | Optimiza el meshing de chunks fuera de la lÃ­nea de visiÃ³n directa. Recomendado mantener activo. |
| **AdaptiveMode** *(confirmado en GUI, no documentado antes)* | Ajusta `MaxRebuilds`/`RebuildDelay` dinÃ¡micamente en tiempo real segÃºn el FPS actual, en vez de usar valores fijos. |
| **TargetFPS** *(confirmado en GUI, no documentado antes)* | FPS objetivo que `AdaptiveMode` intenta mantener al ajustar los rebuilds (por defecto `240`). |
| **MinRebuilds** *(confirmado en GUI, no documentado antes)* | Suelo mÃ­nimo de reconstrucciones por frame que `AdaptiveMode` no bajarÃ¡ aunque el FPS vaya sobrado (por defecto `2`). |

### World (Mundo)
*AutomatizaciÃ³n y dominio del entorno del juego.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **AutoFish** | Detecta el sonido y movimiento del agua cuando un pez muerde el anzuelo, recoge la caÃ±a y vuelve a lanzarla sola. |
| **AutoMine** | Mantiene presionado y activo el botÃ³n de romper bloques de forma constante para facilitar tÃºneles sin cansarte de presionar el ratÃ³n. |
| **AutoTool** | Analiza en microsegundos el bloque que estÃ¡s mirando y cambia tu mano de forma automÃ¡tica a la mejor herramienta de tu inventario. |
| **Scaffold** | Coloca mÃ¡gicamente bloques debajo de tus pies justo a medida que caminas por cornisas o sobre el vacÃ­o, tendiendo puentes mientras te mueves. |
| **VillagerClusters** | Implementa un sistema de clustering para agrupar aldeanos cercanos mostrando mÃ©tricas precisas con lÃ­neas y cajas envolventes en 3D. |
| **Chunks** | Resalta chunks visibles diferenciando los de slimes de los normales. |

**Sub-opciones confirmadas â€” VillagerClusters:**

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **scanRadius** | Radio en bloques donde busca aldeanos a agrupar (por defecto `100`). |
| **clusterDistance** | Distancia mÃ¡xima entre dos aldeanos para considerarlos parte del mismo grupo (por defecto `10`). |
| **minClusterSize** | NÃºmero mÃ­nimo de aldeanos para que se dibuje un cluster (por defecto `3`). |
| **scanInterval** | Cada cuÃ¡ntos ticks se recalculan los clusters (por defecto `20`). |
| **includeZombie** | Si estÃ¡ activo, incluye tambiÃ©n zombie-aldeanos en el conteo/agrupaciÃ³n. |

### Theme (Temas)
*PersonalizaciÃ³n visual de la interfaz del cliente.*

| MÃ³dulo | DescripciÃ³n Exacta |
|---|---|
| **CustomTheme** | Permite cambiar los colores de la interfaz, textos, fondos y degradados de forma interactiva desde el propio juego. |

**Sub-opciones confirmadas â€” CustomTheme:**

| OpciÃ³n | DescripciÃ³n |
|---|---|
| **Grad Start / Grad End** | Colores de inicio y fin del degradado principal de la interfaz, cada uno con selector HSV completo. |
| **Background** | Color/degradado del fondo de los paneles. |
| **Text** | Color del texto de la interfaz. |
| **Active** | Color usado para resaltar mÃ³dulos activos/habilitados (el naranja que se ve en Optimize y Player en tu GUI actual). |

---

## Controles de la Interfaz

- **Abrir MenÃº**: Presiona `Tab + Control` (configurable).
- **Asignar Tecla (Keybind)**: Haz clic derecho en cualquier mÃ³dulo dentro de la GUI y seguidamente presiona la tecla que desees asignar a ese mÃ³dulo.
- **Configurar MÃ³dulo**: Haz clic en el icono de engranaje o despliega la pestaÃ±a de un mÃ³dulo para ajustar opciones extras precisas (sliders numÃ©ricos, opciones de checkbox y menÃºs de modos).
- **Redimensionar Panel** *(nuevo en 8.0.0)*: Arrastra la esquina inferior derecha de cualquier panel de categorÃ­a para ajustar su tamaÃ±o, con scroll interno automÃ¡tico si el contenido no cabe.

---

## CÃ³mo Compilar y Usar

Para construir tu propio archivo `.jar` y disfrutar del mod a partir del cÃ³digo fuente:

1. **Requisitos**: Java 21 o una versiÃ³n superior instalada en tu sistema.
2. Abre la terminal o consola de comandos en esta misma carpeta y ejecuta:
   - **En Windows**: `./gradlew.bat build`
   - **En Linux / macOS**: `./gradlew build`
3. Al terminar, el archivo compilado listo para usar aparecerÃ¡ en la ruta `build/libs/`.
4. Mueve ese archivo `.jar` a tu carpeta local de mods (`%appdata%/.minecraft/mods` en Windows) e inicia el juego asegurÃ¡ndote de usar el perfil cargador de Fabric.

---

> [!WARNING]
> El autor no se responsabiliza de su mal uso en servidores pÃºblicos.

---

##  Changelog

### v8.0.0 â€” 2026-07-24
- **Nuevo MÃ³dulo â€” OrbitCam (Movement)**: CÃ¡mara orbital en tercera persona. Sigue la posiciÃ³n del jugador pero permite girar la vista libremente con el ratÃ³n sin afectar la rotaciÃ³n real (servidor) del jugador. Compatible con KillAura.
- **ClickGUI rediseÃ±ada**: esquinas achaflanadas (chamfer) y bordes con estÃ©tica "tÃ¡ctica".
- **Paneles redimensionables**: arrastra la esquina inferior derecha de cualquier categorÃ­a para ajustar su tamaÃ±o, con scroll interno automÃ¡tico.
- **Persistencia de paneles**: posiciÃ³n y tamaÃ±o de cada panel se guardan automÃ¡ticamente entre sesiones.
- **Mejoras al CrystalAura**: mÃ¡s preciso y fluido, con mejores ejemplos de configuraciÃ³n.
- **Nuevo MÃ³dulo â€” VillagerClusters (World)**: agrupa aldeanos cercanos con lÃ­neas y cajas envolventes en 3D.
- **Nuevo MÃ³dulo â€” LeavesOptimizer (Optimize)**: convierte hojas en bloques sÃ³lidos sin transparencia, color verde uniforme configurable. *(comentado en ModuleManager, ver nota arriba)*
- **Nuevos MÃ³dulos de OptimizaciÃ³n**: `ChunkOptimizer` y `FPSBoost` para reducir microtirones y desactivar renders costosos.
- **Refactor General**: optimizaciÃ³n interna del EventBus.
- PreConfigurado con KillAura y CrystalAura en su mejor configuraciÃ³n.
