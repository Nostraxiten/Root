<div align="center">

# Root v1

### A Fabric utility mod for Minecraft 26.3

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-26.3-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric%20Loader-0.19.5-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Build](https://img.shields.io/badge/Build-SUCCESSFUL-brightgreen.svg?style=for-the-badge)]()

</div>

---

## Overview

**Root v1** is a client-side utility mod for **Minecraft 26.3**. It includes combat automation, movement utilities, render helpers, world tools, and performance optimizers. The GUI is opened with **Ctrl + Tab** in-game.

> [!WARNING]
> The author is not responsible for misuse on public servers. Use at your own risk.

---

## Installation

1. Install [Fabric Loader 0.19.5](https://fabricmc.net/) for Minecraft 26.3.
2. Place `fabric-api-0.161.0+26.3.jar` in your `.minecraft/mods/` folder.
3. Place `RootV1-26.3.jar` in `.minecraft/mods/`.
4. Launch Minecraft 26.3 with the Fabric profile.

**Open GUI:** `Ctrl + Tab`
**Assign keybind:** Right-click a module name → press the key.
**Remove keybind:** `Backspace` or `Delete` while hovering the module.

---

## Modules

###  Combat

| Module | Description | Key Settings |
|---|---|---|
| **KillAura** | Automatically attacks nearby entities in range. | Range, Walls Range, FOV, Target Mode, Priority, Max Targets, Rotation Mode, Weapon Check, Shield Mode, Auto Switch, Swap Back, Ignore Named/Passive/Tamed, Pause On Lag/Use, TPS Sync, Cooldown %, Humanized Timing, Jitter, Switch Delay, Smooth Rotation, Rotation Speed, Aim Noise |
| **CrystalAura** | Automatic end crystal placement and detonation. | Place Range, Break Range, Target Range, Rotation, Timing, Anti-Surround |
| **AutoBlock** | Automatically shields when taking damage. | — |
| **AutoTotem** | Keeps a totem of undying in the offhand automatically. | — |
| **AutoDodge** | Attempts to dodge incoming projectiles. | — |
| **AntiKnockback** | Reduces or cancels knockback received. | Horizontal, Vertical |
| **HitboxExpand** | Expands entity hitboxes for easier hits. | Expand Amount |
| **Surround** | Places obsidian around you for protection. | — |
| **TriggerBot** | Attacks automatically when crosshair is on a valid target. | Delay, Target Mode |

###  Movement

| Module | Description | Key Settings |
|---|---|---|
| **Speed** | Increases movement speed. | Speed, Mode |
| **Fly** | Allows flying in survival. | Speed |
| **NoFall** | Prevents fall damage. | — |
| **Spider** | Allows climbing any block surface. | — |
| **Jesus** | Walk on water. | — |
| **Step** | Steps up full blocks instantly. | Height |
| **AntiVoid** | Prevents falling into the void. | — |
| **AutoSprint** | Automatically sprints. | — |
| **NoclipTP** | Fly through blocks; teleports to destination on disable. | Speed, Safety Check |
| **OrbitCam** | Orbits the camera around the player. | Radius, Speed |
| **Sit** | Sit on the ground using a Cushion entity. | — |
| **Freecam** | Detaches the camera for free exploration. | Speed |

###  Render

| Module | Description | Key Settings |
|---|---|---|
| **ESP** | Draws boxes around players, hostiles, animals and self. | Players, Hostiles, Animals, Self ESP — each with custom color |
| **Tracers** | Draws lines from your screen to entities. | Players, Hostiles, Animals — each with color |
| **BlockESP** | Highlights specific blocks. | Block list, Color |
| **StorageESP** | Highlights storage containers (chests, barrels, etc.). | Color |
| **ChestClusters** | Shows clusters of nearby chests on screen. | Min Cluster Size, Radius |
| **XRay** | Reveals ores, storage, fluids and spawners through walls. | Ores, Storage, Fluids, Spawners, FullBright |
| **ProjectileTrajectory** | Displays the trajectory arc of throwable items. | Steps, Color |
| **CustomOutline** | Draws a custom outline on the targeted block. | Color, Width, Fill, Fill Alpha, Rainbow |
| **NoFog** | Removes all fog from the world. | — |
| **NoParticles** | Disables all particles. | — |
| **Zoom** | Zooms the camera in. | — |
| **FPSBoost** | Applies client-side render optimizations. | — |
| **ChunkOptimizer** | Reduces chunk update load. | — |
| **NightVision** | Gives full night vision. | Strength |

###  World

| Module | Description |
|---|---|
| **AutoMine** | Automatically mines the block you look at. |
| **AutoFarm** | Automatically harvests and replants crops. |
| **AutoTool** | Switches to the best tool for the block being mined. |
| **Scaffold** | Places blocks under your feet while walking. |
| **FastPlace** | Removes the delay between placing blocks. |
| **VillagerClusters** | Shows clusters of nearby villagers on screen. |
| **Chunks** | Renders chunk borders and chunk stats. |

###  Player

| Module | Description | Key Settings |
|---|---|---|
| **AutoEat** | Automatically eats food when hungry. | Hunger Threshold, Health Threshold |
| **AutoArmor** | Equips the best armor from your inventory. | — |
| **AutoFish** | Automates fishing (cast and pull). | — |
| **ChestStealer** | Takes all items from an open chest automatically. | Delay |
| **AntiAFK** | Prevents AFK kick by simulating activity. | — |
| **AutoClicker** | Clicks automatically at a set rate. | CPS, Right Click |
| **AutoRespawn** | Clicks Respawn automatically on death. | — |
| **XPBOT** | Automates XP bottle farming. | — |
| **PanicKey** | Disables all active modules instantly. | Key (keybind) |
| **DeathCoords** | Saves and shows coordinates on death. | Auto Save Waypoint |
| **HUDOverlay** | Shows FPS, coordinates and biome on-screen. | Coordinates, FPS, Biome |
| **Notifications** | Shows on-screen popups when modules are toggled. | Duration (s) |

###  Optimize

| Module | Description |
|---|---|
| **LeavesOptimizer** | Reduces leaf block render lag. |
| **FPSBoost** | Client-side render optimizations. |
| **ChunkOptimizer** | Reduces chunk update load. |

###  Theme

| Module | Description |
|---|---|
| **CustomTheme** | Customizes the GUI accent color and style. |

---

## Build from Source

**Requirements:** Java 25 · Gradle 9.7.0 · Fabric Loom 1.18.2

```powershell
$env:JAVA_HOME = ".\jdk25\jdk-25.0.4+7"
$env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=C:\t"
.\gradlew.bat build
```

Output: `RootCode\build\libs\RootV1-26.3.jar`

---

## Technical Notes

- **Mappings:** Official Mojang mappings (no Yarn/intermediary).
- **Input:** `com.mojang.blaze3d.platform.InputConstants` — single-int key polling, native to MC 26.3's SDL/RenderPearl layer.
- **Render pipeline:** `SubmitNodeCollector` + `RenderPipelines` (Blaze3D RenderPearl).
- **Config persistence:** Saved automatically on every toggle and on game close → `.minecraft/root-config/modules.json`.
- **Keybinds:** Right-click any module in the GUI to assign a key. `Backspace`/`Delete` to clear.

---

<div align="center">
<sub>Root v1 &nbsp;·&nbsp; Minecraft 26.3 &nbsp;·&nbsp; Fabric 0.19.5 &nbsp;·&nbsp; Java 25</sub>
</div>
