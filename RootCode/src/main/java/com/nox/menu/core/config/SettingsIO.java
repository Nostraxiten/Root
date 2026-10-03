package com.nox.menu.core.config;

import com.google.gson.*;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.setting.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger("noxmenu");

    private static JsonObject cachedConfig = new JsonObject();
    private static boolean isLoaded = false;

    /** Remove-then-add so Gson never throws on duplicate keys. */
    private static void safeAdd(JsonObject obj, String key, JsonElement value) {
        if (obj.has(key)) obj.remove(key);
        obj.add(key, value);
    }

    private static Path getConfigPath() {
        Path configDir = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("noxmenu");
        try {
            Files.createDirectories(configDir, new FileAttribute[0]);
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory", (Throwable)e);
        }
        return configDir.resolve("modules.json");
    }

    private static synchronized void ensureLoaded() {
        if (isLoaded) return;
        Path path = SettingsIO.getConfigPath();
        LOGGER.info("[RootV7] ensureLoaded: reading from {}", path);
        if (Files.exists(path, new LinkOption[0])) {
            try {
                String existing = Files.readString(path, StandardCharsets.UTF_8);
                cachedConfig = JsonParser.parseString(existing).getAsJsonObject();
                LOGGER.info("[RootV7] ensureLoaded: loaded {} keys from disk", cachedConfig.size());
            } catch (Exception e) {
                LOGGER.warn("[RootV7] ensureLoaded: could not parse config, starting fresh", e);
                cachedConfig = new JsonObject();
            }
        } else {
            LOGGER.info("[RootV7] ensureLoaded: no config file found at {}", path);
        }
        isLoaded = true;
    }

    private static synchronized void flushToDisk() {
        Path path = SettingsIO.getConfigPath();
        try {
            String json = GSON.toJson(cachedConfig);
            Files.writeString(path, json, StandardCharsets.UTF_8, new OpenOption[0]);
            LOGGER.info("[RootV7] flushToDisk: wrote {} bytes to {}", json.length(), path);
        } catch (IOException e) {
            LOGGER.error("[RootV7] flushToDisk: FAILED to write config", (Throwable)e);
        }
    }

    public static synchronized void save(ModuleManager manager) {
        ensureLoaded();
        LOGGER.info("[RootV7] save() called - saving {} modules", manager.getModules().size());
        for (Module module : manager.getModules()) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("enabled", Boolean.valueOf(module.isEnabled()));
            moduleObj.addProperty("keyBind", (Number)module.getKeyBind());
            JsonObject settingsObj = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                try {
                    if (setting instanceof BooleanSetting) {
                        settingsObj.addProperty(setting.getName(), (Boolean)setting.getValue());
                    } else if (setting instanceof NumberSetting) {
                        settingsObj.addProperty(setting.getName(), (Number)setting.getValue());
                    } else if (setting instanceof ModeSetting) {
                        settingsObj.addProperty(setting.getName(), (String)setting.getValue());
                    } else if (setting instanceof ColorSetting) {
                        settingsObj.addProperty(setting.getName(), (Number)setting.getValue());
                    } else {
                        settingsObj.addProperty(setting.getName(), String.valueOf(setting.getValue()));
                    }
                } catch (Exception e) {
                    LOGGER.error("[RootV7] save: error serializing setting {} of {}: {}", setting.getName(), module.getName(), e.getMessage());
                }
            }
            moduleObj.add("settings", (JsonElement)settingsObj);
            safeAdd(cachedConfig, module.getName(), (JsonElement)moduleObj);
        }
        flushToDisk();
    }

    public static synchronized void load(ModuleManager manager) {
        ensureLoaded();
        JsonObject root = cachedConfig;
        LOGGER.info("[RootV7] load() called - config has {} keys", root.size());
        if (root.entrySet().isEmpty()) {
            LOGGER.info("[RootV7] load: no config data, using defaults");
            return;
        }
        try {
            for (Module module : manager.getModules()) {
                if (!root.has(module.getName())) continue;
                JsonObject moduleObj = root.getAsJsonObject(module.getName());
                if (moduleObj.has("settings")) {
                    JsonObject settingsObj = moduleObj.getAsJsonObject("settings");
                    for (Setting<?> setting : module.getSettings()) {
                        if (!settingsObj.has(setting.getName())) continue;
                        JsonElement element = settingsObj.get(setting.getName());
                        try {
                            if (setting instanceof BooleanSetting) {
                                ((BooleanSetting)setting).setValue(element.getAsBoolean());
                            } else if (setting instanceof NumberSetting) {
                                ((NumberSetting)setting).setValue(element.getAsDouble());
                            } else if (setting instanceof ModeSetting) {
                                ModeSetting ms = (ModeSetting)setting;
                                String mode = element.getAsString();
                                if (ms.getModes().contains(mode)) ms.setValue(mode);
                            } else if (setting instanceof ColorSetting) {
                                ((ColorSetting)setting).setValue(element.getAsInt());
                            }
                        } catch (Exception e) {
                            LOGGER.warn("[RootV7] load: failed setting {} for {}: {}", setting.getName(), module.getName(), e.getMessage());
                        }
                    }
                }
                try {
                    if (moduleObj.has("enabled") && moduleObj.get("enabled").getAsBoolean()) {
                        module.setEnabled(true, true);
                    }
                    if (moduleObj.has("keyBind")) {
                        module.setKeyBind(moduleObj.get("keyBind").getAsInt());
                    }
                } catch (Exception e) {
                    LOGGER.warn("[RootV7] load: failed enabled/keyBind for {}: {}", module.getName(), e.getMessage());
                }
            }
            LOGGER.info("[RootV7] load: finished loading config");
        } catch (Exception e) {
            LOGGER.error("[RootV7] load: CRITICAL FAILURE", (Throwable)e);
        }
    }

    public static synchronized void saveUI(java.util.List<com.nox.menu.gui.components.CategoryPanel> panels) {
        ensureLoaded();
        JsonObject ui = new JsonObject();
        for (com.nox.menu.gui.components.CategoryPanel panel : panels) {
            JsonObject p = new JsonObject();
            p.addProperty("x", panel.getX());
            p.addProperty("y", panel.getY());
            ui.add(panel.getCategory().name(), p);
        }
        safeAdd(cachedConfig, "_ui", ui);
        flushToDisk();
    }

    public static synchronized void loadUI(java.util.List<com.nox.menu.gui.components.CategoryPanel> panels) {
        ensureLoaded();
        JsonObject root = cachedConfig;
        if (!root.has("_ui")) return;
        try {
            JsonObject ui = root.getAsJsonObject("_ui");
            for (com.nox.menu.gui.components.CategoryPanel panel : panels) {
                String key = panel.getCategory().name();
                if (!ui.has(key)) continue;
                try {
                    JsonObject p = ui.getAsJsonObject(key);
                    if (p.has("x")) panel.setX(p.get("x").getAsInt());
                    if (p.has("y")) panel.setY(p.get("y").getAsInt());
                } catch (Exception e) {
                    LOGGER.warn("[RootV7] loadUI: malformed entry for panel {}", key, e);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("[RootV7] loadUI: could not load UI layout", e);
        }
    }
}