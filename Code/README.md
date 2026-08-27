<div align="center">

# Root — 26.2

*Puerto en progreso del cliente Root para Minecraft 26.2 (Fabric)*

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Estado](https://img.shields.io/badge/Estado-Fase%201%20(l%C3%B3gica%2C%20sin%20GUI)-yellow.svg?style=for-the-badge)]()

</div>

## Descripción General

Este es el port de **Root** (basado en la versión 1.21.11) a **Minecraft 26.2** ("Chaos Cubed"), la última versión estable de Java Edition en el nuevo esquema de versionado por año de Mojang.

> **Importante:** esta carpeta **no toca** `RootClient 1.21.11` ni `RootClient 1.21.5` — es un port independiente con su propio `RootCode`, `RootMod` y `OLDvers`.

## Por qué este port fue distinto a un simple recompilado

A partir de 26.2, **Mojang dejó de ofuscar el cliente de Java** (el código se distribuye con los nombres de clases/métodos reales). Por eso Fabric decidió **dejar de mantener Yarn** desde esta versión — ya no hace falta "traducir" nombres ofuscados. Esto significa:

- `build.gradle` ya no declara un artefacto `mappings` (Loom resuelve el mapeo oficial de Mojang automáticamente).
- Todo el código de `RootCode` que antes usaba nombres de Yarn (`MinecraftClient`, `ClientPlayerEntity`, `Box`, `Vec3d`, `PlayerEntity`, etc.) se reescribió a los nombres oficiales de Mojang (`Minecraft`, `LocalPlayer`, `AABB`, `Vec3`, `Player`, etc.), verificados contra el `client.jar` real de 26.2 (no adivinados).
- Se requiere **Java 25** (antes 21) y **Gradle 9.5.1** / **Fabric Loom 1.17**.
- Mojang también rehízo el pipeline interno de renderizado (nuevo sistema `SubmitNodeCollector`/`RenderPipelines`, reemplazando `RenderType`/`VertexConsumer`/`DrawContext`), y Fabric API reescribió sus hooks de render/HUD. Esto afecta directamente a la fase 2 (ver abajo).

## Estado actual: Fase 1 (lógica de combate/movimiento/utilidad — sin GUI ni visuales)

Dado el cambio de pipeline de render, el port se hizo en dos fases:

### ✅ Fase 1 — Completada (este build)
Todo lo que **no dibuja nada en pantalla** está portado y debería compilar: el framework interno (`Module`, `ModuleManager`, `EventBus`, `Settings`), los mixins de lógica de red/entidad/bloques, y los siguientes módulos:

**Combat:** KillAura, CrystalAura, Surround, AutoBlock, TriggerBot, AntiKnockback, AutoTotem, HitboxExpand, AutoDodge
**Movement:** Speed, Fly, NoFall, Spider, Jesus, Step, AntiVoid, AutoSprint, NoclipTP, OrbitCam
**Player:** AutoRespawn, AutoEat, AutoArmor, AntiAFK, ChestStealer, InvMove, NameProtect, XPBOT, PanicKey, AutoClicker
**World:** AutoMine, Scaffold, FastPlace, AutoFish, AutoTool, AutoFarm, TimeChanger, AntiWeather, Waypoints, NightVision
**Render (no visual):** Freecam (solo cámara), NoFog, NoParticles, Zoom, CustomOutline*, FPSBoost*, ChunkOptimizer* (*: la parte de ajustes existe, el efecto real depende de mixins de render aún pendientes)
**Theme:** CustomTheme (solo almacena los valores; sin GUI que los use todavía)

### ⏳ Fase 2 — Pendiente
Todo lo que dibuja en pantalla vive en `PENDING_PORT_RENDER/` (junto a `RootCode`, fuera de `src/`, para que no rompa la compilación) hasta investigar a fondo la nueva API de render de 26.2:
- **ClickGUI completo** (`gui/` — el menú entero, sus componentes y `RenderUtil`)
- **ESP, Tracers, BlockESP, StorageESP, ChestClusters, VillagerClusters, Chunks, ProjectileTrajectory, XRay, LeavesOptimizer**
- **Sit, DeathCoords, HUDOverlay, NotificationSystem**
- Mixins de render: `MixinGameRenderer`, `MixinWorldRenderer`, `MixinInGameHud`, `MixinInGameOverlayRenderer`, `MixinItemEntityRenderer(Static)`, `MixinItemRendererGlint`, `MixinRenderLayers`, `MixinRenderTickCounter`, `MixinSpriteContents`, `MixinLightmapTextureManager`, `MixinParticleManager`, `MixinBackgroundRenderer`, `MixinBlockColors`, `MixinCamera`, `MixinEntityRenderDispatcher`, `MixinBlock`/`MixinBlockBehaviour` (dependen de XRay)

**Sin la GUI**, de momento la única forma de activar un módulo es editando `config/noxmenu/modules.json` (poner `"enabled": true` en el módulo que quieras) y reiniciando el juego — no hay atajos de teclado por defecto ni menú en pantalla todavía.

## Compilación

Requiere **JDK 25** y **Gradle 9.5.1** (el wrapper `gradlew` ya apunta a esa versión). No hay carpeta `jdk21`/`gradle-8.11.1` pre-empaquetada como en la 1.21.11 — descárgalos si tu entorno no los tiene:

```powershell
$env:JAVA_HOME="<ruta a tu JDK 25>"; .\gradlew.bat build
```

El `.jar` compilado aparece en `RootCode\build\libs\`.

> **Nota:** este build **no se ha compilado todavía** en este entorno (requiere descargar JDK 25 + Gradle 9.5.1 + el cliente/mappings de 26.2 vía Loom, que no se hizo en esta sesión). Es muy probable que la primera compilación señale 2-3 detalles menores (algún nombre de método de Mixin que no coincida exactamente) — son ajustes rápidos y esperables en cualquier port de este tamaño a una versión con mapeos completamente nuevos.

## Controles

Sin ClickGUI todavía, no hay atajo para abrir un menú. Los módulos que tengan `keyBind` guardado en `modules.json` de una sesión anterior seguirán respondiendo a esa tecla (gestionado por `MixinMinecraft`).

---

> [!WARNING]
> El autor no se responsabiliza de su mal uso en servidores públicos.
