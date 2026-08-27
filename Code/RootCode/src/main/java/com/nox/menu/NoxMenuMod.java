package com.nox.menu;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.gui.ClickGUIScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Open the menu with Ctrl+Tab, same shortcut as 1.21.5/1.21.11.
public class NoxMenuMod
implements ClientModInitializer {
    public static final String MOD_ID = "noxmenu";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"noxmenu");
    private static NoxMenuMod INSTANCE;
    private ModuleManager moduleManager;
    private boolean wasGuiKeyPressed = false;

    public static NoxMenuMod getInstance() {
        return INSTANCE;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public void onInitializeClient() {
        INSTANCE = this;
        LOGGER.info("NoxMenu initializing...");
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            this.handleGuiKey(client);
            this.moduleManager.onTick();
        });

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            this.moduleManager.onWorldRender(context.poseStack(), context.submitNodeCollector(), tickDelta);
        });

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "overlay"), (context, tickDelta) ->
                this.moduleManager.onHudRender(context, tickDelta.getGameTimeDeltaPartialTick(true)));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            if (this.moduleManager != null) {
                this.moduleManager.save();
            }
        });

        LOGGER.info("NoxMenu initialized with {} modules", this.moduleManager.getModules().size());
    }

    private void handleGuiKey(Minecraft client) {
        long handle = client.getWindow().handle();
        boolean tabPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_TAB) == 1;
        boolean ctrlPressed = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == 1 || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == 1;
        boolean pressed = tabPressed && ctrlPressed;
        if (pressed && !this.wasGuiKeyPressed) {
            if (client.gui.screen() instanceof ClickGUIScreen) {
                if (this.moduleManager != null) {
                    this.moduleManager.save();
                }
                client.gui.setScreen(null);
            } else if (client.gui.screen() == null) {
                client.gui.setScreen(new ClickGUIScreen());
            }
        }
        this.wasGuiKeyPressed = pressed;
    }
}
