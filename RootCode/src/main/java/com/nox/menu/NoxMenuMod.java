package com.nox.menu;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.gui.ClickGUIScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// el menú se abre con Ctrl+Tab, igual que en 1.21.5/1.21.11
public class NoxMenuMod
implements ClientModInitializer {
    public static final String MOD_ID = "noxmenu";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"noxmenu");
    private static NoxMenuMod INSTANCE;
    private ModuleManager moduleManager;
    // evita que un tick con la tecla ya pulsada vuelva a abrir/cerrar el menú
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
        boolean tabPressed = com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_TAB);
        boolean ctrlPressed =
            com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_LCONTROL) ||
            com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_RCONTROL);
        boolean pressed = tabPressed && ctrlPressed;
        if (pressed && !this.wasGuiKeyPressed) {
            if (client.gui.screen() instanceof ClickGUIScreen) {
                // Si el GUI de Root está abierto, cerrarlo
                if (this.moduleManager != null) this.moduleManager.save();
                client.gui.setScreen(null);
            } else {
                // Abrir Root GUI (funciona tanto desde el juego como desde el pause menu)
                client.gui.setScreen(new ClickGUIScreen());
            }
        }
        this.wasGuiKeyPressed = pressed;
    }
}
