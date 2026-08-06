<div align="center">

# Root

*Modular client for Minecraft 1.21.5 (Fabric)*

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-green.svg?style=for-the-badge)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue.svg?style=for-the-badge)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-10.0.0-purple.svg?style=for-the-badge)]()

<img width="974" height="258" alt="image" src="https://github.com/user-attachments/assets/443b4de4-0b67-4a3a-8f15-c1d9babd8f35" />

</div>

## Overview

**Root** is a client under active development for Minecraft 1.21.5. Any bug you find can be reported in the repository so it can be fixed in upcoming releases.

> **Important note about the repository (GitHub):**
> When uploaded to GitHub, the private source code (`RootCode`) is not included in the public repository.
> The repository only contains the following folders:
> - **`RootMod`**: Contains the main, stable, ready-to-use version, already exported in installable format.
> - **`OLDvers`**: Contains old or experimental versions. Some work fine, but others may be unstable, have bugs, or crash the game. Use them with caution.

<img width="1822" height="477" alt="image" src="https://github.com/user-attachments/assets/9850f771-4727-4de8-8fad-7c36c8222197" />

## Modules and Options

The menu features an extensive list of modules carefully organized by category. Below is a detailed explanation of **exactly what each option does**.

### Combat
*Modules focused on assistance during combat and damage.*

| Module | Exact Description |
|---|---|
| **AntiKnockback** | Nullifies or reduces knockback when hit, preventing you from being pushed or knocked into the void. |
| **AutoBlock** | Automatically blocks attacks using your sword or shield at the exact moment damage is received. |
| **AutoDodge** | Automatically dodges arrows and projectiles by detecting their trajectory. |
| **AutoTotem** | Instantly and automatically equips a Totem of Undying in your off-hand when your health is critical. |
| **CrystalAura** | Automatically places and detonates end crystals to deal area damage to your enemies. More accurate and smooth than previous versions. |
| **HitboxExpand** | Virtually enlarges the area in which you can hit enemies, making it much easier to land hits. Does not affect your own character. |
| **KillAura** | Automatically and rapidly attacks and hits any entity or player that enters your attack range. |
| **Surround** | Automatically places blocks around you to protect your feet from explosions. |
| **TriggerBot** | Automatically hits the instant your crosshair crosses an entity. |

**Extra settings (Combat / KillAura / CrystalAura):**

| Option | Description |
|---|---|
| **Engage Range** | Radius in blocks from which the module starts attacking/acting on a target (default `8.0`). |
| **Max Engage Distance** | Maximum distance at which a target is still considered "engaged" before releasing it. |
| **Placement Offset** | Position adjustment relative to the target when placing the crystal (CrystalAura). |
| **Crystal Interval (ms)** | Minimum time between placing/detonating consecutive crystals. |
| **Anchor Drift Tolerance** | Allowed movement margin of the target before recalculating where the crystal is placed. |
| **Airborne Abort Ticks** | Wait time before canceling the action if the target is airborne (avoids failures from jumping/knockback). |
| **Approach Timeout** | Maximum time it tries to approach an out-of-range target before giving up. |
| **Target Players / Target Mobs / Target Animals / Target Hostile** | Independent checkboxes to choose which entity type counts as a valid target. |
| **Auto Move** | Automatically moves toward the target if it is out of range. |
| **Move Speed** | Speed of that automatic movement (default `0.3`). |
| **Auto Jump (anti-stuck)** | Automatically jumps if it detects it is stuck against a block. |
| **Throw Pearl On Engage** | Automatically throws an ender pearl when engaging a target. |
| **Pearl Min Distance** | Minimum distance to the target for the pearl throw to be allowed. |
| **Rotation Ease** | Smoothing of the camera turn when aiming (lower = more abrupt, higher = smoother). |
| **Rotation Jitter** | Small variations added to the turn so it looks less automatic. |
| **Smooth Camera** | Enables overall smooth camera movement for the module. |

**Settings — Surround:**

| Option | Description |
|---|---|
| **Place Below** | Also includes the block beneath your feet within the protection ring, not just the sides. |

**Other modules with their own settings**: `TriggerBot`, `AntiKnockback`, `AutoTotem`, `HitboxExpand`, `AutoDodge`.

### Movement
*Modules to alter your movement through the world.*

| Module | Exact Description |
|---|---|
| **AntiVoid** | Automatically saves you from falling into the void by teleporting you upward or bouncing you to give you a chance to save yourself. |
| **AutoSprint** | Keeps sprint mode automatically enabled whenever you move forward. |
| **Fly** | Lets you fly freely through the world in survival mode as if you were playing in creative mode. |
| **Jesus** | Walk and run on the surface of water or lava without sinking, with a proper jump and without floating excessively. |
| **NoclipTP** | Lets you pass through walls and solid blocks safely to teleport to the other side. |
| **NoFall** | Completely eliminates fall damage when jumping or falling from great heights. |
| **Sit** | Sit on the ground anywhere in the world without needing chairs or special blocks. |
| **Speed** | Increases your movement speed beyond what's normal in the game. |
| **Spider** | Lets you climb any vertical wall of solid blocks as if you were climbing a ladder. |
| **Step** | Increases step height, letting you climb stairs or full blocks instantly without needing to jump. |
| **OrbitCam** | Third-person camera that orbits around your character without changing where you're actually looking in the game. Simulates a free view without losing control of your player — works alongside KillAura while you rotate the camera with the mouse. Ready for use with bots. |

**Confirmed settings:**

| Module | Option | Description |
|---|---|---|
| **Speed** | Ground Multiplier | Speed multiplier while on the ground (default `3.0`). |
| **Speed** | Air Multiplier | Speed multiplier while in the air (default `2.0`). |
| **Fly** | Speed | Flight speed (default `4.0`). |
| **OrbitCam** | Radius | Orbital distance of the camera relative to the player (default `12.0`). |
| **OrbitCam** | Sensitivity | Mouse sensitivity while orbiting (default `3.0`). |
| **OrbitCam** | Collision | If enabled, the camera does not pass through solid blocks while orbiting. |
| **OrbitCam** | Smoothing | Smooth orbital movement instead of abrupt jumps. |

**Other modules with their own settings**: `Spider`, `Step`, `AntiVoid`, `NoclipTP`.

### Player
*Improvements to the player's own interaction and utilities.*

| Module | Exact Description |
|---|---|
| **AntiAFK** | Performs small automatic movements and actions to prevent servers from kicking you for inactivity. |
| **AutoArmor** | Automatically equips the best armor pieces you have in your inventory. |
| **AutoClicker** | Configurable auto clicker: you can adjust the speed, the action type (attack/interact), and add randomness to make it look natural. |
| **AutoEat** | Selects the best food in your inventory and eats automatically when your hunger or health level drops. |
| **AutoRespawn** | Skips the "You Died" screen and instantly brings you back to life. |
| **ChestStealer** | Quickly and automatically transfers all the contents of a chest to your inventory as soon as you open it. Configurable speed (instant if you prefer). |
| **DeathCoords** | Saves and displays the exact coordinates of your last death in chat. |
| **HUDOverlay** | Displays essential real-time information on screen such as coordinates, FPS, lag, armor, and active modules. |
| **NotificationSystem** | Shows elegant, non-intrusive on-screen alerts about client actions and events. |
| **PanicKey** | Works as an emergency button that disables every active module with a single press to appear 100% legitimate. |

**Confirmed settings:**

| Module | Option | Description |
|---|---|---|
| **AntiAFK** | Min Interval / Max Interval | Time range between each automatic micro-movement, randomly chosen within that range (default `40`–`100`). |
| **AntiAFK** | Look Noise | Amount of variation applied to the camera turn on each micro-movement, to simulate a human (default `1.0`). |
| **AntiAFK** | Strafe Ticks | Duration of each simulated sideways movement (default `10`). |
| **AutoEat** | Hunger Threshold | Hunger level (out of 20) below which it starts eating automatically (default `15`). |
| **ChestStealer** | Delay | Wait time between each steal. At 0, it empties the entire chest instantly. |
| **HUDOverlay** | Coordinates / FPS / Biome | Independent checkboxes to show or hide each piece of on-screen data. |
| **DeathCoords** | Auto Save Waypoint | If enabled, automatically saves the last death as a waypoint instead of just showing it in chat. |

**Other modules with their own settings**: `AutoArmor`, `NotificationSystem`.

### Render (Visuals)
*Graphical alterations and visual improvements to how you see the world.*

| Module | Exact Description |
|---|---|
| **BlockESP** | Highlights ore blocks (Diamond, Ancient Debris, Gold, Iron, Coal, Emerald, Copper) through walls with specific colored lines and a configurable radius. |
| **ChestClusters** | Automatically groups nearby chests, trapped chests, and barrels. Draws a line to the center of the group, a box surrounding it, and a label with the chest count. |
| **ESP** | Draws boxes around players, hostile mobs, animals, and your own character (visible only in 2nd/3rd person camera). Independent colors per category. |
| **Freecam** | Detaches the camera from your body to freely explore the surroundings as a spectator, while your character stays safely in place. |
| **ProjectileTrajectory** | Draws a line predicting exactly where your arrows or projectiles will land before you throw/shoot them. |
| **StorageESP** | Finds and draws outlines on vaults, chests, barrels, furnaces, and shulkers, seeing them through the ground or walls. |
| **Tracers** | Draws colored lines from your crosshair to players and mobs. Independent colors per category, option for animals, configurable max distance. |
| **XRay** | Makes all common valueless blocks invisible, instantly revealing only valuable ores. |
| **Zoom** | Zooms in the camera view with a smooth, fluid transition, configurable. |

**Settings:**

| Module | Option | Description |
|---|---|---|
| **XRay** | Ores / Storage / Fluids / Spawners / FullBright | Independent checkboxes: what to reveal through blocks and whether to force full brightness while active. |
| **ESP** | Players / Hostiles / Animals / Self ESP | Checkboxes to filter which entity type gets a highlight box. Your own character is only visible in 2nd/3rd person camera. |
| **ESP** | Player Color / Hostile Color / Animal Color / Self Color | Independent color pickers per category. |
| **Tracers** | Players / Hostiles / Animals | Checkboxes to filter who gets a line drawn from the crosshair. |
| **Tracers** | Player Color / Hostile Color / Animal Color | Independent color pickers. |
| **Tracers** | Max Distance | Maximum distance in blocks to draw the line (0 = unlimited). |
| **Zoom** | Speed | Speed of the zoom transition (default `4.0`). |

**Other modules with their own settings**: `Freecam`, `StorageESP`, `BlockESP`, `ChestClusters`.

### Optimize
*Performance and FPS improvements for the client.*

| Module | Exact Description |
|---|---|
| **LeavesOptimizer** | Removes the detailed rendering of tree leaves and replaces them with solid, uniformly colored green blocks. Notably improves performance in forested areas. |
| **NoFog** | Completely removes distance fog, deep water fog, and lava fog, greatly improving visibility. |
| **NoParticles** | Completely removes all in-game particles, greatly helping to boost FPS and improve performance. |

#### FPSBoost
Optimizations to improve FPS.

| Option | Description |
|---|---|
| **NoWeather** | Completely disables the rendering of rain and snow. |
| **NoClouds** | Disables cloud rendering. |
| **NoMenuBlur** | Removes the blur effect when opening menus. |
| **AnimationThrottle** | Reduces how often animations are updated (animated blocks, water, lava, portals, etc.). |
| **AnimSkipTicks** | How many animation steps are skipped before showing the next one (0–3). The higher the value, the more static the animation looks. |
| **StaticAnim** | Completely freezes texture animation (100% static). Maximum performance savings. Takes priority over AnimationThrottle. |
| **LowFire** | Reduces the size of the fire effect while burning. |
| **FireScale** | Scale of the fire effect (0.1 = minimum, 1.0 = normal). |
| **StaticDrops** | Freezes the rotation of dropped items on the ground. |
| **LimitEntities** | Enables a custom render distance for entities. |
| **EntityDist** | Maximum distance in blocks at which entities are rendered. Ones farther away are not shown. |
| **FastGlint** | Simplifies the enchantment glint on items to save performance. |

#### ChunkOptimizer
Optimizes terrain loading and rendering.

| Option | Description |
|---|---|
| **MaxRebuilds** | Maximum number of terrain sections the game can update per frame (default `15`). Higher values load terrain faster but can cause stutters. |
| **RebuildDelay** | Delay in milliseconds between terrain updates (default `50`). Increasing it smooths out stutters on slower computers. |
| **LazyChunks** | Optimizes the rendering of terrain that isn't directly in view. Recommended to keep enabled. |
| **AdaptiveMode** | Automatically adjusts `MaxRebuilds`/`RebuildDelay` in real time based on current FPS, instead of using fixed values. |
| **TargetFPS** | Target FPS that `AdaptiveMode` tries to maintain when adjusting updates (default `240`). |
| **MinRebuilds** | Minimum updates per frame that `AdaptiveMode` will not go below even if FPS is plenty (default `2`). |

### World
*Automation and mastery of the game environment.*

| Module | Exact Description |
|---|---|
| **AutoFish** | Detects when a fish bites the hook, reels in the rod, and casts it again on its own. |
| **AutoMine** | Keeps the block-breaking button continuously active to make tunneling easier without tiring your mouse hand. |
| **AutoTool** | Instantly detects the block you're looking at and automatically switches your hand to the best tool in your inventory. |
| **Chunks** | Advanced terrain visualizer. Shows the boundaries of each map area in flat mode (at eye height) or full column mode. Distinguishes special slime-fishing chunks (green) from normal ones (blue). Shows a label and the entity count per area. Configurable radius and options. |
| **NightVision** | Simulates permanent night vision without needing any potion. Lights up the world 100% in any dimension. No on-screen icon, no duration limit, undetectable by the server. Configurable intensity. |
| **Scaffold** | Magically places blocks beneath your feet exactly as you walk along ledges or over the void, bridging as you move. |
| **VillagerClusters** | Automatically groups nearby villagers, showing precise metrics with lines and boxes surrounding them in 3D. |

**Settings — Chunks:**

| Option | Description |
|---|---|
| **Radius** | How many chunks in each direction are displayed around the player (1–16, default `4`). |
| **Slime Chunks** | Enables differentiation of slime-fishing chunks (green) from normal chunks (blue). In singleplayer it uses the real world seed; in multiplayer it is calculated approximately. |
| **Entity Count** | Shows the number of loaded entities within each chunk above it. |
| **Flat Mode** | If enabled, draws only the square at eye height with corner markers (like the game's classic outline). If disabled, draws the full column from bedrock to the world's ceiling. |

**Settings — NightVision:**

| Option | Description |
|---|---|
| **Strength** | Night vision intensity (0.1–1.0, default `1.0` = maximum brightness). Intermediate values give a subtler effect. |

**Confirmed settings — VillagerClusters:**

| Option | Description |
|---|---|
| **scanRadius** | Radius in blocks in which it searches for villagers to group (default `100`). |
| **clusterDistance** | Maximum distance between two villagers for them to be considered part of the same group (default `10`). |
| **minClusterSize** | Minimum number of villagers for a group to be drawn (default `3`). |
| **scanInterval** | How often the groups are recalculated (default `20`). |
| **includeZombie** | If enabled, also includes zombie villagers in the count/grouping. |

### Theme
*Visual customization of the client's interface.*

| Module | Exact Description |
|---|---|
| **CustomTheme** | Lets you interactively change the menu's colors, text, backgrounds, and gradients from within the game itself. |

**Confirmed settings — CustomTheme:**

| Option | Description |
|---|---|
| **Grad Start / Grad End** | Start and end colors of the menu's main gradient, each with a full color picker. |
| **Background** | Color/gradient of the panel backgrounds. |
| **Text** | Menu text color. |
| **Active** | Color used to highlight active/enabled modules. |

---

## Menu Controls

- **Open Menu**: Press `Tab + Control`.
- **Bind Key**: Right-click any module inside the menu, then press the key you want to bind to it.
- **Configure Module**: Click the gear icon or expand a module's tab to adjust its extra options (numeric sliders, checkboxes, and mode menus).
- **Resize Panel**: Drag the bottom-right corner of any category panel to adjust its size, with automatic internal scrolling if the content doesn't fit.


> [!WARNING]
> The author is not responsible for its misuse on public servers.

---

## What's New

### v10.0.0 — RootV10

#### New Modules
- **NightVision (World)**: Permanent night vision without needing a potion. No on-screen icon, undetectable by the server, no duration limit. Configurable intensity (0.1–1.0). Compatible with real potion night vision (they can coexist without issues). Works in all dimensions: Overworld, Nether, End, underwater, in caves.
- **Sit (Movement)**: Sit on the ground anywhere in the world without needing chairs or special blocks.
- **AutoClicker (Player)**: Auto clicker with configurable speed, action type (attack/interact), and randomness to make it look natural.

#### Major Improvements
- **ESP**: Completely rebuilt, with box rendering now working correctly. Added a highlight option for your own character, with its own color, only visible in 2nd/3rd person camera. Independent colors per category (Players, Hostiles, Animals, Self).
- **Tracers v2**: Rebuilt so lines render correctly, with independent colors per category, a new option for animals, and a max distance setting.
- **Chunks v2**: Full terrain visualizer. Flat mode (at eye height, with corner markers) and full column mode. Differentiation of slime-fishing chunks. Entity count per chunk. Configurable radius (1–16). Real seed in singleplayer.
- **HitboxExpand**: Fixed so it no longer affects your own character.
- **Jesus**: Rewritten with more precise movement calculation. Fixed the floating bug after jumping. Correctly detects the water surface. Support for lava without burning.
- **ChestStealer**: Fixed a bug that caused errors when calculating quantities.
