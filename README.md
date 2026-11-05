# Orbit HUD

A client-side Fabric mod for Minecraft 1.21.1 with a PvP-style HUD and a Right Shift mod menu, in the spirit of Lunar Client.

## Features

- **Keystrokes**: W/A/S/D, LMB/RMB with live CPS, and the space bar. Labels follow your keybinds.
- **CPS counter**: exact clicks per second, counted from raw mouse input (not tick-limited).
- **FPS, Ping, and Coordinates** (with facing direction).
- **Minimap**: top-down terrain map with vanilla map colors and a rotating player arrow.
- **Mob Health**: health bar for the mob you're looking at, or the last one you hit, with a gradient and absorption.
- **Boss Health**: restyled, movable boss bars. Can hide the vanilla ones.
- **Armor Status** (with colored durability) and **Potion Effects** (with icons and timers).
- **Mod menu (Right Shift)**: toggle modules, edit text and background colors with RGBA sliders, chroma (rainbow) mode, scale, and per-module options. A **Theme** card sets the menu accent and can apply colors to every module at once.
- **Layout editor**: drag modules to move them, scroll over one to resize it. Snaps to screen edges and center.
- Everything is saved to `config/orbithud.json`.

## Build

The mod compiles and runs on **Java 21**. Newer versions crash on launch because this Fabric Loader's Mixin can't read their class files (`Unsupported class file major version 67`). You don't have to install 21 yourself or change `JAVA_HOME`: the Gradle toolchain uses JDK 21 for both compiling and `runClient`, and downloads it on first run if it's missing.

```bash
./gradlew runClient   # launches a dev Minecraft with the mod
./gradlew build       # jar ends up in build/libs/orbithud-1.0.0.jar
```

On Windows, use `gradlew.bat`. You can also open the folder in IntelliJ IDEA (it imports as a Gradle project).

To play with it, put the jar and [Fabric API](https://modrinth.com/mod/fabric-api) into your `.minecraft/mods` folder, using a Fabric 1.21.1 profile.

If Gradle can't resolve a version, get the current 1.21.1 values from https://fabricmc.net/develop and update `gradle.properties`.

## Project layout

```
OrbitHud.java            entrypoint: keybind, HUD render callback, tick, attack hook
config/HudConfig.java    JSON save/load
hud/HudModule.java       base class: position, scale, colors, chroma, flags
hud/TextModule.java      single-line text modules
hud/ModuleManager.java   module list, render loop, clamping/centering
hud/CpsTracker.java      1-second sliding window of clicks
hud/modules/*            each HUD element
gui/ModMenuScreen.java   Right Shift menu (cards, sliders, toggles)
gui/HudEditorScreen.java drag/scale layout editor
mixin/*                  mouse input (CPS), boss bar access, hiding vanilla boss bars
```

## Adding a module

Extend `TextModule` (or `HudModule` for custom drawing), call `defineFlag(...)` for any options, and register it in `ModuleManager.init()`. The menu, editor, and config pick it up automatically.
