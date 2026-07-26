<div align="center">

# Root

*Cliente modular para Minecraft 1.21.5 (Fabric)*

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-10.0.0-purple.svg?style=for-the-badge)]()

<img width="1881" height="836" alt="image" src="https://github.com/user-attachments/assets/1dd7306d-52e6-4a7d-abbe-49e54d8ef7b7" />

</div>

## Descripción General

**Root** es un cliente en desarrollo activo para Minecraft 1.21.5. Cualquier fallo que encuentres puedes reportarlo en el repositorio para que se solucione en próximas versiones.

> **Nota importante sobre el repositorio (GitHub):**
> Al subirse a GitHub, el código fuente privado (`RootCode`) no se incluye en el repositorio público.
> El repositorio solo contiene las siguientes carpetas:
> - **`RootMod`**: Contiene la versión principal, estable y lista para usar, ya exportada en formato instalable.
> - **`OLDvers`**: Contiene versiones antiguas o experimentales. Algunas funcionan bien, pero otras pueden ser inestables, tener fallos o cerrar el juego. Úsalas con precaución.

<img width="1822" height="477" alt="image" src="https://github.com/user-attachments/assets/9850f771-4727-4de8-8fad-7c36c8222197" />

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
| **CrystalAura** | Coloca y explota cristales del end automáticamente para hacer daño de área a tus enemigos. Más preciso y fluido que versiones anteriores. |
| **HitboxExpand** | Agranda virtualmente el área en la que puedes golpear a los enemigos, facilitando enormemente acertar los golpes. No afecta a tu propio personaje. |
| **KillAura** | Ataca y golpea automáticamente y a gran velocidad a cualquier entidad o jugador que entre en tu radio de alcance. |
| **Surround** | Coloca bloques automáticamente a tu alrededor para proteger tus pies de explosiones. |
| **TriggerBot** | Golpea automáticamente justo en el instante en el que tu mira se cruza con una entidad. |

**Ajustes extra (Combate / KillAura / CrystalAura):**

| Opción | Descripción |
|---|---|
| **Engage Range** | Radio en bloques a partir del cual el módulo empieza a atacar/actuar sobre un objetivo (por defecto `8,0`). |
| **Max Engage Distance** | Distancia máxima a la que se sigue considerando "enganchado" a un objetivo antes de soltarlo. |
| **Placement Offset** | Ajuste de posición respecto al objetivo al colocar el cristal (CrystalAura). |
| **Crystal Interval (ms)** | Tiempo mínimo entre colocación/explosión de cristales consecutivos. |
| **Anchor Drift Tolerance** | Margen de movimiento permitido del objetivo antes de recalcular dónde se coloca el cristal. |
| **Airborne Abort Ticks** | Tiempo de espera antes de cancelar la acción si el objetivo queda en el aire (evita fallos por salto/retroceso). |
| **Approach Timeout** | Tiempo máximo que intenta acercarse a un objetivo fuera de rango antes de rendirse. |
| **Target Players / Target Mobs / Target Animals / Target Hostile** | Casillas independientes para elegir qué tipo de entidad se considera un objetivo válido. |
| **Auto Move** | Se mueve automáticamente hacia el objetivo si está fuera de alcance. |
| **Move Speed** | Velocidad de ese desplazamiento automático (por defecto `0,3`). |
| **Auto Jump (anti-stuck)** | Salta automáticamente si detecta que se ha quedado atascado contra un bloque. |
| **Throw Pearl On Engage** | Lanza una perla de ender automáticamente al enganchar un objetivo. |
| **Pearl Min Distance** | Distancia mínima al objetivo para que se permita lanzar la perla. |
| **Rotation Ease** | Suavizado del giro de cámara al apuntar (menor = más brusco, mayor = más suave). |
| **Rotation Jitter** | Pequeñas variaciones añadidas al giro para que parezca menos automático. |
| **Smooth Camera** | Activa el movimiento suave de cámara en general para el módulo. |

**Ajustes — Surround:**

| Opción | Descripción |
|---|---|
| **Place Below** | Incluye también el bloque bajo tus pies dentro del anillo de protección, no solo los laterales. |

**Otros módulos con ajustes propios**: `TriggerBot`, `AntiKnockback`, `AutoTotem`, `HitboxExpand`, `AutoDodge`.

### Movement (Movimiento)
*Módulos para alterar tu desplazamiento por el mundo.*

| Módulo | Descripción Exacta |
|---|---|
| **AntiVoid** | Te salva automáticamente de caer al vacío teletransportándote hacia arriba o rebotando para darte la oportunidad de salvarte. |
| **AutoSprint** | Mantiene activado el modo de correr automáticamente siempre que te mueves hacia adelante. |
| **Fly** | Te permite volar libremente por el mundo en modo supervivencia como si estuvieras jugando en modo creativo. |
| **Jesus** | Camina y corre sobre la superficie del agua o la lava sin hundirte, con un salto correcto y sin flotar de más. |
| **NoclipTP** | Te permite atravesar paredes y bloques sólidos de forma segura para teletransportarte al otro lado. |
| **NoFall** | Elimina por completo el daño por caída al saltar o caer desde grandes alturas. |
| **Sit** | Siéntate en el suelo en cualquier lugar del mundo sin necesidad de sillas ni bloques especiales. |
| **Speed** | Aumenta tu velocidad de movimiento por encima de lo normal en el juego. |
| **Spider** | Te permite trepar cualquier pared vertical de bloques sólidos como si estuvieras subiendo una escalera de mano. |
| **Step** | Aumenta la altura de paso, permitiéndote subir escalones o bloques completos de forma instantánea sin tener que saltar. |
| **OrbitCam** | Cámara en tercera persona que gira alrededor de tu personaje sin cambiar hacia dónde miras realmente en el juego. Simula una vista libre sin perder el control de tu jugador — funciona a la vez que KillAura mientras giras la cámara con el ratón. Preparado para uso con bots. |

**Ajustes confirmados:**

| Módulo | Opción | Descripción |
|---|---|---|
| **Speed** | Ground Multiplier | Multiplicador de velocidad mientras estás en el suelo (por defecto `3,0`). |
| **Speed** | Air Multiplier | Multiplicador de velocidad mientras estás en el aire (por defecto `2,0`). |
| **Fly** | Speed | Velocidad de vuelo (por defecto `4,0`). |
| **OrbitCam** | Radius | Distancia orbital de la cámara respecto al jugador (por defecto `12,0`). |
| **OrbitCam** | Sensitivity | Sensibilidad del ratón al orbitar (por defecto `3,0`). |
| **OrbitCam** | Collision | Si está activo, la cámara no atraviesa bloques sólidos al orbitar. |
| **OrbitCam** | Smoothing | Movimiento orbital suave en vez de saltos bruscos. |

**Otros módulos con ajustes propios**: `Spider`, `Step`, `AntiVoid`, `NoclipTP`.

### Player (Jugador)
*Mejoras en la interacción y utilidades del propio jugador.*

| Módulo | Descripción Exacta |
|---|---|
| **AntiAFK** | Realiza pequeños movimientos y acciones automáticas para evitar que los servidores te expulsen por inactividad. |
| **AutoArmor** | Equipa automáticamente las mejores piezas de armadura que tengas en tu inventario. |
| **AutoClicker** | Clicker automático configurable: puedes ajustar la velocidad, el tipo de acción (atacar/interactuar) y añadir aleatoriedad para que resulte natural. |
| **AutoEat** | Selecciona la mejor comida de tu inventario y come de manera automática cuando tu nivel de hambre o salud disminuye. |
| **AutoRespawn** | Salta la pantalla de "Has muerto" y te devuelve a la vida al instante. |
| **ChestStealer** | Transfiere rápidamente y de forma automática todo el contenido de un cofre a tu inventario nada más abrirlo. Velocidad configurable (instantáneo si lo prefieres). |
| **DeathCoords** | Guarda y muestra las coordenadas exactas de tu última muerte en el chat. |
| **HUDOverlay** | Muestra en pantalla información esencial en tiempo real como coordenadas, FPS, lag, armadura y módulos activos. |
| **NotificationSystem** | Muestra alertas visuales elegantes y no intrusivas en pantalla sobre acciones y eventos del cliente. |
| **PanicKey** | Funciona como un botón de emergencia que desactiva de un solo golpe todos los módulos activos para parecer 100% legítimo. |

**Ajustes confirmados:**

| Módulo | Opción | Descripción |
|---|---|---|
| **AntiAFK** | Min Interval / Max Interval | Rango de tiempo entre cada micromovimiento automático, elegido al azar dentro de ese rango (por defecto `40`–`100`). |
| **AntiAFK** | Look Noise | Cantidad de variación aplicada al giro de cámara en cada micromovimiento, para simular un humano (por defecto `1,0`). |
| **AntiAFK** | Strafe Ticks | Duración de cada movimiento lateral simulado (por defecto `10`). |
| **AutoEat** | Hunger Threshold | Nivel de hambre (sobre 20) por debajo del cual empieza a comer automáticamente (por defecto `15`). |
| **ChestStealer** | Delay | Tiempo de espera entre cada robo. En 0, vacía el cofre entero de inmediato. |
| **HUDOverlay** | Coordinates / FPS / Biome | Casillas independientes para mostrar u ocultar cada dato en pantalla. |
| **DeathCoords** | Auto Save Waypoint | Si está activo, guarda automáticamente la última muerte como punto de referencia en vez de solo mostrarla por chat. |

**Otros módulos con ajustes propios**: `AutoArmor`, `NotificationSystem`.

### Render (Visuales)
*Alteraciones gráficas y mejoras visuales de cómo ves el mundo.*

| Módulo | Descripción Exacta |
|---|---|
| **BlockESP** | Resalta bloques de minerales (Diamante, Ancestral, Oro, Hierro, Carbón, Esmeralda, Cobre) a través de las paredes con líneas de colores específicos y radio configurable. |
| **ChestClusters** | Agrupa cofres, cofres trampa y barriles cercanos automáticamente. Dibuja una línea hacia el centro del grupo, una caja que lo rodea y una etiqueta con el número de cofres. |
| **ESP** | Dibuja cajas alrededor de jugadores, criaturas hostiles, animales y tu propio personaje (visible solo en cámara de 2ª/3ª persona). Colores independientes por categoría. |
| **Freecam** | Suelta la cámara de tu cuerpo para explorar los alrededores libremente como espectador, mientras tu personaje se queda seguro donde está. |
| **ProjectileTrajectory** | Dibuja una línea que predice exactamente dónde caerán tus flechas o proyectiles antes de lanzarlos. |
| **StorageESP** | Encuentra y dibuja contornos sobre cajas fuertes, cofres, barriles, hornos y shulkers, viéndolos a través del suelo o paredes. |
| **Tracers** | Dibuja líneas de colores desde tu mira hacia jugadores y mobs. Colores independientes por categoría, opción para animales, distancia máxima configurable. |
| **XRay** | Hace invisibles todos los bloques comunes sin valor, dejando ver al instante solo los minerales valiosos. |
| **Zoom** | Acerca la vista de la cámara con una transición fluida y suave, configurable. |

**Ajustes:**

| Módulo | Opción | Descripción |
|---|---|---|
| **XRay** | Ores / Storage / Fluids / Spawners / FullBright | Casillas independientes: qué revelar a través de los bloques y si forzar brillo total mientras está activo. |
| **ESP** | Players / Hostiles / Animals / Self ESP | Casillas para filtrar qué tipo de entidad recibe la caja de resalte. Tu propio personaje solo se ve en cámara de 2ª/3ª persona. |
| **ESP** | Player Color / Hostile Color / Animal Color / Self Color | Selectores de color independientes por categoría. |
| **Tracers** | Players / Hostiles / Animals | Casillas para filtrar a quién se le dibuja la línea desde la mira. |
| **Tracers** | Player Color / Hostile Color / Animal Color | Selectores de color independientes. |
| **Tracers** | Max Distance | Distancia máxima en bloques para dibujar la línea (0 = ilimitado). |
| **Zoom** | Speed | Velocidad de la transición al hacer zoom (por defecto `4,0`). |

**Otros módulos con ajustes propios**: `Freecam`, `StorageESP`, `BlockESP`, `ChestClusters`.

### Optimize (Optimización)
*Mejoras de rendimiento y FPS para el cliente.*

| Módulo | Descripción Exacta |
|---|---|
| **LeavesOptimizer** | Elimina el dibujado detallado de las hojas de los árboles y las sustituye por bloques sólidos de color verde uniforme. Mejora notablemente el rendimiento en zonas boscosas. |
| **NoFog** | Elimina completamente la niebla de la lejanía, del agua profunda y de la lava, mejorando enormemente la visibilidad. |
| **NoParticles** | Elimina por completo todas las partículas del juego, ayudando inmensamente a subir los FPS y mejorar el rendimiento. |

#### FPSBoost
Optimizaciones para mejorar los FPS.

| Opción | Descripción |
|---|---|
| **NoWeather** | Desactiva el dibujado de lluvia y nieve por completo. |
| **NoClouds** | Desactiva el dibujado de nubes. |
| **NoMenuBlur** | Elimina el efecto de desenfoque al abrir menús. |
| **AnimationThrottle** | Reduce la frecuencia con la que se actualizan las animaciones (bloques animados, agua, lava, portales, etc.). |
| **AnimSkipTicks** | Cuántos pasos de animación se saltan antes de mostrar el siguiente (0–3). Cuanto mayor, más estática se ve la animación. |
| **StaticAnim** | Congela completamente la animación de texturas (100% estática). Máximo ahorro de rendimiento. Tiene prioridad sobre AnimationThrottle. |
| **LowFire** | Reduce el tamaño del efecto de fuego al estar quemándose. |
| **FireScale** | Escala del efecto de fuego (0.1 = mínimo, 1.0 = normal). |
| **StaticDrops** | Congela la rotación de los objetos tirados en el suelo. |
| **LimitEntities** | Activa una distancia de dibujado personalizada para entidades. |
| **EntityDist** | Distancia máxima en bloques a la que se dibujan las entidades. Las que estén más lejos no se muestran. |
| **FastGlint** | Simplifica el brillo de los objetos encantados para ahorrar rendimiento. |

#### ChunkOptimizer
Optimiza la carga y el dibujado del terreno.

| Opción | Descripción |
|---|---|
| **MaxRebuilds** | Máximo de secciones de terreno que el juego puede actualizar por fotograma (por defecto `15`). Valores más altos cargan el terreno más rápido pero pueden causar tirones. |
| **RebuildDelay** | Retardo en milisegundos entre actualizaciones de terreno (por defecto `50`). Aumentarlo suaviza los tirones en ordenadores más lentos. |
| **LazyChunks** | Optimiza el dibujado del terreno que no está directamente a la vista. Recomendado mantener activo. |
| **AdaptiveMode** | Ajusta `MaxRebuilds`/`RebuildDelay` automáticamente en tiempo real según el FPS actual, en vez de usar valores fijos. |
| **TargetFPS** | FPS objetivo que `AdaptiveMode` intenta mantener al ajustar las actualizaciones (por defecto `240`). |
| **MinRebuilds** | Mínimo de actualizaciones por fotograma que `AdaptiveMode` no bajará aunque el FPS vaya sobrado (por defecto `2`). |

### World (Mundo)
*Automatización y dominio del entorno del juego.*

| Módulo | Descripción Exacta |
|---|---|
| **AutoFish** | Detecta cuándo un pez muerde el anzuelo, recoge la caña y vuelve a lanzarla sola. |
| **AutoMine** | Mantiene activo el botón de romper bloques de forma constante para facilitar túneles sin cansarte de presionar el ratón. |
| **AutoTool** | Detecta al instante el bloque que estás mirando y cambia tu mano de forma automática a la mejor herramienta de tu inventario. |
| **Chunks** | Visualizador avanzado del terreno del juego. Muestra los bordes de cada zona del mapa en modo plano (a la altura de los ojos) o en modo columna completa. Diferencia zonas especiales para pesca de slime (verde) de las normales (azul). Muestra una etiqueta y el número de entidades por zona. Radio y opciones configurables. |
| **NightVision** | Simula visión nocturna permanente sin necesidad de tomar ninguna poción. Ilumina el mundo al 100% en cualquier dimensión. Sin icono en pantalla, sin límite de duración, indetectable por el servidor. Intensidad configurable. |
| **Scaffold** | Coloca mágicamente bloques debajo de tus pies justo a medida que caminas por cornisas o sobre el vacío, tendiendo puentes mientras te mueves. |
| **VillagerClusters** | Agrupa automáticamente a los aldeanos cercanos mostrando métricas precisas con líneas y cajas que los rodean en 3D. |

**Ajustes — Chunks:**

| Opción | Descripción |
|---|---|
| **Radius** | Cuántas zonas en cada dirección se visualizan alrededor del jugador (1–16, por defecto `4`). |
| **Slime Chunks** | Activa la diferenciación de zonas de pesca de slime (verde) frente a zonas normales (azul). En partidas de un jugador usa la semilla real del mundo; en multijugador se calcula de forma aproximada. |
| **Entity Count** | Muestra sobre cada zona el número de entidades cargadas dentro de ella. |
| **Flat Mode** | Si está activo, dibuja solo el cuadrado a la altura de los ojos con marcas en las esquinas (como el visor clásico del juego). Si está desactivado, dibuja la columna completa desde el fondo al techo del mundo. |

**Ajustes — NightVision:**

| Opción | Descripción |
|---|---|
| **Strength** | Intensidad de la visión nocturna (0.1–1.0, por defecto `1.0` = brillo máximo). Valores intermedios dan un efecto más sutil. |

**Ajustes confirmados — VillagerClusters:**

| Opción | Descripción |
|---|---|
| **scanRadius** | Radio en bloques donde busca aldeanos a agrupar (por defecto `100`). |
| **clusterDistance** | Distancia máxima entre dos aldeanos para considerarlos parte del mismo grupo (por defecto `10`). |
| **minClusterSize** | Número mínimo de aldeanos para que se dibuje un grupo (por defecto `3`). |
| **scanInterval** | Cada cuánto tiempo se recalculan los grupos (por defecto `20`). |
| **includeZombie** | Si está activo, incluye también zombis-aldeanos en el conteo/agrupación. |

### Theme (Temas)
*Personalización visual de la interfaz del cliente.*

| Módulo | Descripción Exacta |
|---|---|
| **CustomTheme** | Permite cambiar los colores del menú, textos, fondos y degradados de forma interactiva desde el propio juego. |

**Ajustes confirmados — CustomTheme:**

| Opción | Descripción |
|---|---|
| **Grad Start / Grad End** | Colores de inicio y fin del degradado principal del menú, cada uno con selector de color completo. |
| **Background** | Color/degradado del fondo de los paneles. |
| **Text** | Color del texto del menú. |
| **Active** | Color usado para resaltar módulos activos/habilitados. |

---

## Controles del Menú

- **Abrir Menú**: Presiona `Tab + Control`.
- **Asignar Tecla**: Haz clic derecho en cualquier módulo dentro del menú y seguidamente presiona la tecla que desees asignarle.
- **Configurar Módulo**: Haz clic en el icono de engranaje o despliega la pestaña de un módulo para ajustar sus opciones extra (deslizadores numéricos, casillas y menús de modos).
- **Redimensionar Panel**: Arrastra la esquina inferior derecha de cualquier panel de categoría para ajustar su tamaño, con desplazamiento interno automático si el contenido no cabe.


> [!WARNING]
> El autor no se responsabiliza de su mal uso en servidores públicos.

---

## Novedades

### v10.0.0 — RootV10 — 2026-07-26

#### Nuevos Módulos
- **NightVision (Mundo)**: Visión nocturna permanente sin necesidad de poción. Sin icono en pantalla, indetectable por el servidor, sin límite de duración. Intensidad configurable (0.1–1.0). Compatible con la visión nocturna real de una poción (pueden coexistir sin problema). Funciona en todas las dimensiones: Overworld, Nether, End, bajo el agua, en cuevas.
- **Sit (Movimiento)**: Siéntate en el suelo en cualquier lugar del mundo sin necesidad de sillas ni bloques especiales.
- **AutoClicker (Jugador)**: Clicker automático con velocidad configurable, tipo de acción (ataque/interacción) y aleatoriedad para que resulte natural.

#### Mejoras Mayores
- **ESP**: Rehecho por completo, con dibujado de cajas funcionando correctamente. Añadida opción de resalte para tu propio personaje, con color propio, solo visible en cámara de 2ª/3ª persona. Colores independientes por categoría (Jugadores, Hostiles, Animales, Propio).
- **Tracers v2**: Rehecho para que las líneas se dibujen correctamente, con colores independientes por categoría, nueva opción para animales y ajuste de distancia máxima.
- **Chunks v2**: Visualizador completo del terreno. Modo plano (a la altura de los ojos, con marcas en las esquinas) y modo columna completa. Diferenciación de zonas de pesca de slime. Conteo de entidades por zona. Radio configurable (1–16). Semilla real en partidas de un jugador.
- **HitboxExpand**: Corregido para que ya no afecte a tu propio personaje.
- **Jesus**: Reescrito con un cálculo de movimiento más preciso. Corregido el fallo de flotar tras saltar. Detecta la superficie del agua correctamente. Soporte para lava sin quemarte.
- **ChestStealer**: Corregido un fallo que provocaba errores al calcular cantidades.
