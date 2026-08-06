<div align="center">

# Root

*Modular Client for Minecraft 1.21.5 (Fabric)*

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-10.0.0-purple.svg?style=for-the-badge)]()

<img width="974" height="258" alt="image" src="https://github.com/user-attachments/assets/443b4de4-0b67-4a3a-8f15-c1d9babd8f35" />

</div>

## Overview

**Root** is an actively developed client for Minecraft 1.21.5. Any bugs you encounter can be reported in the repository to be fixed in upcoming versions.

> **Important note about the repository (GitHub):**
> When uploaded to GitHub, the private source code (`RootCode`) is not included in the public repository.
> The repository only contains the following folders:
> - **`RootMod`**: Contains the main version, stable and ready to use, already exported in installable format.
> - **`OLDvers`**: Contains old or experimental versions. Some work well, but others may be unstable, have bugs, or crash the game. Use them with caution.

<img width="1822" height="477" alt="image" src="https://github.com/user-attachments/assets/9850f771-4727-4de8-8fad-7c36c8222197" />

## Modules and Options

The menu features an extensive list of modules carefully organized by category. Below details **exactly what each option does**.

### Combat
*Modules focused on combat assistance and damage.*

| Module | Exact Description |
|---|---|
| **AntiKnockback** | Nullifies or reduces knockback when hit, preventing you from being pushed or knocked into the void. |
| **AutoBlock** | Automatically blocks attacks using your sword or shield at the exact moment of taking damage. |
| **AutoDodge** | Automatically dodges arrows and projectiles by detecting their trajectory. |
| **AutoTotem** | Instantly equips a Totem of Immortality in your offhand automatically when your health is critical. |
| **CrystalAura** | Automatically places and detonates end crystals to deal area damage to your enemies. More precise and fluid than previous versions. |
| **HitboxExpand** | Virtually expands the area where you can hit enemies, making it much easier to land hits. Does not affect your own character. |
| **KillAura** | Automatically attacks and hits any entity or player entering your reach radius at high speed. |
| **Surround** | Automatically places blocks around you to protect your feet from explosions. |
| **TriggerBot** | Automatically hits the instant your crosshair crosses an entity. |

**Extra Settings (Combat / KillAura / CrystalAura):**

| Option | Description |
|---|---|
| **Engage Range** | Radius in blocks from which the module starts attacking/acting on a target (default `8.0`). |
| **Max Engage Distance** | Maximum distance at which a target is still considered "engaged" before releasing it. |
| **Placement Offset** | Position adjustment relative to the target when placing a crystal (CrystalAura). |
| **Crystal Interval (ms)** | Minimum time between consecutive crystal placements/detonations. |
| **Anchor Drift Tolerance** | Allowed movement margin for the target before recalculating crystal placement. |
| **Airborne Abort Ticks** | Wait time before canceling the action if the target becomes airborne (prevents failures from jumping/knockback). |
| **Approach Timeout** | Maximum time to attempt approaching an out-of-range target before giving up. |
| **Target Players / Target Mobs / Target Animals / Target Hostile** | Independent checkboxes to choose what entity type counts as a valid target. |
| **Auto Move** | Automatically moves toward the target if it's out of range. |
| **Move Speed** | Speed of that automatic movement (default `0.3`). |
| **Auto Jump (anti-stuck)** | Automatically jumps if it detects getting stuck against a block. |
| **Throw Pearl On Engage** | Automatically throws an ender pearl when engaging a target. |
| **Pearl Min Distance** | Minimum distance to target for pearl throwing to be allowed. |
| **Rotation Ease** | Smoothing of camera rotation when aiming (lower = jerkier, higher = smoother). |
| **Rotation Jitter** | Small variations added to rotation to make it look less automatic. |
| **Smooth Camera** | Enables smooth camera movement overall for the module. |

**Surround Settings:**

| Option | Description |
|---|---|
| **Place Below** | Also includes the block under your feet in the protective ring, not just the sides. |

**Other modules with own settings**: `TriggerBot`, `AntiKnockback`, `AutoTotem`, `HitboxExpand`, `AutoDodge`.

### Movement
*Modules to alter your movement through the world.*

| Module | Exact Description |
|---|---|
| **AntiVoid** | Automatically saves you from falling into the void by teleporting you upward or bouncing to give you a chance to escape. |
| **AutoSprint** | Automatically keeps sprint mode enabled whenever you move forward. |
| **Fly** | Allows you to fly freely through the world in survival mode as if you were playing in creative mode. |
| **Jesus** | Walk and run on the surface of water or lava without sinking, with proper jumping and no excessive floating. |
| **NoclipTP** | Allows you to safely pass through walls and solid blocks to teleport to the other side. |
| **NoFall** | Completely eliminates fall damage when jumping or falling from great heights. |
| **Sit** | Sit on the ground anywhere in the world without needing chairs or special blocks. |
| **Speed** | Increases your movement speed above normal game speed. |
| **Spider** | Allows you to climb any vertical wall of solid blocks as if you were climbing a ladder. |
| **Step** | Increases step height, allowing you to climb stairs or full blocks instantly without having to jump. |
| **OrbitCam** | Third-person camera that orbits around your character without changing where you actually look in the game. Simulates a free view without losing player control — works alongside KillAura while rotating the camera with the mouse. Ready for bot use. |

**Confirmed Settings:**

| Module | Option | Description |
|---|---|---|
| **Speed** | Ground Multiplier | Speed multiplier while on ground (default `3.0`). |
| **Speed** | Air Multiplier | Speed multiplier while in the air (default `2.0`). |
| **Fly** | Speed | Flight speed (default `4.0`). |
| **OrbitCam** | Radius | Orbital distance of the camera from the player (default `12.0`). |
| **OrbitCam** | Sensitivity | Mouse sensitivity when orbiting (default `3.0`). |
| **OrbitCam** | Collision | If active, the camera doesn't pass through solid blocks when orbiting. |
| **OrbitCam** | Smoothing | Smooth orbital movement instead of jerky jumps. |

**Other modules with own settings**: `Spider`, `Step`, `AntiVoid`, `NoclipTP`.

### Player
*Improvements to player interaction and utilities.*

| Module | Exact Description |
|---|---|
| **AntiAFK** | Performs small automatic movements and actions to prevent servers from kicking you for inactivity. |
| **AutoArmor** | Automatically equips the best armor pieces you have in your inventory. |
| **AutoClicker** | Configurable autoclicker: you can adjust speed, action type (attack/interact), and add randomness for natural feel. |
| **AutoEat** | Selects the best food from your inventory and automatically eats when your hunger or health decreases. |
| **AutoRespawn** | Skips the "You Died" screen and instantly brings you back to life. |
| **ChestStealer** | Quickly and automatically transfers all contents of a chest to your inventory as soon as it's opened. Configurable speed (instant if you prefer). |
| **DeathCoords** | Saves and displays the exact coordinates of your last death in chat. |
| **HUDOverlay** | Displays essential information in real-time on screen like coordinates, FPS, lag, armor, and active modules. |
| **NotificationSystem** | Shows elegant and non-intrusive visual alerts on screen about client actions and events. |
| **PanicKey** | Works as an emergency button that disables all active modules at once to appear 100% legitimate. |

**Confirmed Settings:**

| Module | Option | Description |
|---|---|---|
| **AntiAFK** | Min Interval / Max Interval | Time range between each automatic micro-movement, randomly chosen within that range (default `40`–`100`). |
| **AntiAFK** | Look Noise | Amount of variation applied to camera rotation in each micro-movement, to simulate a human (default `1.0`). |
| **AntiAFK** | Strafe Ticks | Duration of each simulated strafe movement (default `10`). |
| **AutoEat** | Hunger Threshold | Hunger level (out of 20) below which it starts eating automatically (default `15`). |
| **ChestStealer** | Delay | Wait time between each theft. At 0, empties the entire chest instantly. |
| **HUDOverlay** | Coordinates / FPS / Biome | Independent checkboxes to show or hide each piece of data on screen. |
| **DeathCoords** | Auto Save Waypoint | If active, automatically saves the last death as a waypoint instead of just displaying it in chat. |

**Other modules with own settings**: `AutoArmor`, `NotificationSystem`.

### Render
*Visual alterations and visual improvements to how you see the world.*

| Module | Exact Description |
|---|---|
| **BlockESP** | Highlights mineral blocks (Diamond, Amethyst, Gold, Iron, Coal, Emerald, Copper) through walls with specific colored lines and configurable radius. |
| **ChestClusters** | Automatically groups nearby chests, trapped chests, and barrels. Draws a line to the center of the group, a box around it, and a label with the chest count. |
| **ESP** | Draws boxes around players, hostile creatures, animals, and your own character (only visible in 2nd/3rd person camera). Independent colors per category. |
| **Freecam** | Detaches the camera from your body to freely explore your surroundings as a spectator while your character stays safe where it is. |
| **ProjectileTrajectory** | Draws a line predicting exactly where your arrows or projectiles will land before you shoot them. |
| **StorageESP** | Finds and draws outlines on safes, chests, barrels, furnaces, and shulkers, seeing them through ground or walls. |
| **Tracers** | Draws colored lines from your crosshair to players and mobs. Independent colors per category, option for animals, configurable maximum distance. |
| **XRay** | Makes all worthless common blocks invisible, instantly showing only valuable minerals. |
| **Zoom** | Zooms the camera view with a smooth and fluid transition, configurable. |

**Settings:**

| Module | Option | Description |
|---|---|---|
| **XRay** | Ores / Storage / Fluids / Spawners / FullBright | Independent checkboxes: what to reveal through blocks and whether to force full brightness while active. |
| **ESP** | Players / Hostiles / Animals / Self ESP | Checkboxes to filter which entity type receives the highlight box. Your own character only shows in 2nd/3rd person camera. |
| **ESP** | Player Color / Hostile Color / Animal Color / Self Color | Independent color pickers per category. |
| **Tracers** | Players / Hostiles / Animals | Checkboxes to filter who gets a line drawn from the crosshair. |
| **Tracers** | Player Color / Hostile Color / Animal Color | Independent color pickers. |
| **Tracers** | Max Distance | Maximum distance in blocks to draw the line (0 = unlimited). |
| **Zoom** | Speed | Speed of the zoom transition (default `4.0`). |

**Other modules with own settings**: `Freecam`, `StorageESP`, `BlockESP`, `ChestClusters`.

### Optimize
*Performance and FPS improvements for the client.*

| Module | Exact Description |
|---|---|
| **LeavesOptimizer** | Removes detailed rendering of tree leaves and replaces them with solid blocks of uniform green color. Notably improves performance in forested areas. |
| **NoFog** | Completely removes distance fog, deep water fog, and lava fog, greatly improving visibility. |
| **NoParticles** | Completely removes all game particles, greatly helping to increase FPS and improve performance. |

#### FPSBoost
Optimizations to improve FPS.

| Option | Description |
|---|---|
| **NoWeather** | Completely disables rain and snow rendering. |
| **NoClouds** | Disables cloud rendering. |
| **NoMenuBlur** | Removes the blur effect when opening menus. |
| **AnimationThrottle** | Reduces the frequency of animation updates (animated blocks, water, lava, portals, etc.). |
| **AnimSkipTicks** | How many animation frames to skip before showing the next one (0–3). Higher = more static animation. |
| **StaticAnim** | Completely freezes texture animation (100% static). Maximum performance savings. Takes priority over AnimationThrottle. |
| **LowFire** | Reduces the size of the fire effect when burning. |
| **FireScale** | Fire effect scale (0.1 = minimum, 1.0 = normal). |
| **StaticDrops** | Freezes the rotation of dropped items on the ground. |
| **LimitEntities** | Enables custom render distance for entities. |
| **EntityDist** | Maximum distance in blocks where entities are rendered. Those farther away don't show. |
| **FastGlint** | Simplifies the shine of enchanted items to save performance. |

#### ChunkOptimizer
Optimizes terrain loading and rendering.

| Option | Description |
|---|---|
| **MaxRebuilds** | Maximum number of terrain sections the game can update per frame (default `15`). Higher values load terrain faster but may cause stuttering. |
| **RebuildDelay** | Delay in milliseconds between terrain updates (default `50`). Increasing it smooths stuttering on slower computers. |
| **LazyChunks** | Optimizes rendering of terrain not directly in view. Recommended to keep active. |
| **AdaptiveMode** | Automatically adjusts `MaxRebuilds`/`RebuildDelay` in real-time based on current FPS, instead of using fixed values. |
| **TargetFPS** | Target FPS that `AdaptiveMode` tries to maintain when adjusting updates (default `240`). |
| **MinRebuilds** | Minimum updates per frame that `AdaptiveMode` won't drop below even if FPS is high (default `2`). |

### World
*Automation and control of the game environment.*

| Module | Exact Description |
|---|---|
| **AutoFish** | Detects when a fish bites the hook, reels in the rod, and casts it again automatically. |
| **AutoMine** | Keeps the block-breaking button active constantly to make tunneling easier without tiring your mouse finger. |
| **AutoTool** | Instantly detects the block you're looking at and automatically switches your hand to the best tool in your inventory. |
| **Chunks** | Advanced game terrain visualizer. Shows the edges of each map zone in flat mode (at eye level) or full column mode. Differentiates special slime-fishing zones (green) from normal zones (blue). Shows a label and entity count per zone. Configurable radius and options. |
| **NightVision** | Simulates permanent night vision without needing to drink any potion. Illuminates the world 100% in any dimension. No screen icon, no duration limit, undetectable by the server. Configurable intensity. |
| **Scaffold** | Magically places blocks under your feet as you walk across ledges or over the void, building bridges as you move. |
| **VillagerClusters** | Automatically groups nearby villagers displaying precise metrics with 3D lines and boxes around them. |

**Chunks Settings:**

| Option | Description |
|---|---|
| **Radius** | How many zones in each direction are visualized around the player (1–16, default `4`). |
| **Slime Chunks** | Enables differentiation of slime-fishing zones (green) vs normal zones (blue). In single-player uses the real world seed; in multiplayer calculated approximately. |
| **Entity Count** | Shows the number of loaded entities within each zone above it. |
| **Flat Mode** | If active, draws only the square at eye level with corner marks (like the classic game reticle). If off, draws the full column from world bottom to ceiling. |

**NightVision Settings:**

| Option | Description |
|---|---|
| **Strength** | Night vision intensity (0.1–1.0, default `1.0` = max brightness). Intermediate values give a more subtle effect. |

**Confirmed VillagerClusters Settings:**

| Option | Description |
|---|---|
| **scanRadius** | Radius in blocks where it searches for villagers to group (default `100`). |
| **clusterDistance** | Maximum distance between two villagers to consider them part of the same group (default `10`). |
| **minClusterSize** | Minimum number of villagers for a group to be drawn (default `3`). |
| **scanInterval** | How often the groups are recalculated (default `20`). |
| **includeZombie** | If active, also includes zombie villagers in the count/grouping. |

### Theme
*Visual customization of the client interface.*

| Module | Exact Description |
|---|---|
| **CustomTheme** | Allows you to change menu colors, text, backgrounds, and gradients interactively from within the game. |

**Confirmed CustomTheme Settings:**

| Option | Description |
|---|---|
| **Grad Start / Grad End** | Start and end colors of the main menu gradient, each with a full color picker. |
| **Background** | Panel background color/gradient. |
| **Text** | Menu text color. |
| **Active** | Color used to highlight active/enabled modules. |

---

## Menu Controls

- **Open Menu**: Press `Tab + Control`.
- **Bind Key**: Right-click any module in the menu and then press the key you want to assign to it.
- **Configure Module**: Click the gear icon or expand a module's tab to adjust its extra options (numeric sliders, checkboxes, and mode menus).
- **Resize Panel**: Drag the bottom-right corner of any category panel to adjust its size, with automatic internal scrolling if content doesn't fit.


> [!WARNING]
> The author is not responsible for misuse on public servers.

---

## Changelog

### v10.0.0 — RootV10

#### New Modules
- **NightVision (World)**: Permanent night vision without needing a potion. No screen icon, undetectable by server, no duration limit. Configurable intensity (0.1–1.0). Compatible with real potion night vision (can coexist without issues). Works in all dimensions: Overworld, Nether, End, underwater, in caves.
- **Sit (Movement)**: Sit on the ground anywhere in the world without needing chairs or special blocks.
- **AutoClicker (Player)**: Autoclicker with configurable speed, action type (attack/interact), and randomness for natural feel.

#### Major Improvements
- **ESP**: Completely remade with correct box rendering. Added highlight option for your own character with its own color, only visible in 2nd/3rd person camera. Independent colors per category (Players, Hostiles, Animals, Self).
- **Tracers v2**: Remade so lines render correctly, with independent colors per category, new animal option, and maximum distance adjustment.
- **Chunks v2**: Complete terrain visualizer. Flat mode (at eye level with corner marks) and full column mode. Slime-fishing zone differentiation. Entity count per zone. Configurable radius (1–16). Real seed in single-player worlds.
- **HitboxExpand**: Fixed so it no longer affects your own character.
- **Jesus**: Rewritten with more precise movement calculation. Fixed floating bug after jumping. Correctly detects water surface. Lava support without burning.
- **ChestStealer**: Fixed a bug that caused calculation errors.
