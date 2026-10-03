/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.lwjgl.glfw.GLFW
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.nox.menu.mixin;

import com.nox.menu.NoxMenuMod;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public abstract class MixinMinecraft {
    private static final Map<Integer, Boolean> prevKeyState = new HashMap<Integer, Boolean>();

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void onTick(CallbackInfo ci) {
        Minecraft client = (Minecraft)(Object)this;
        if (client.player == null || client.gui.screen() != null) {
            return;
        }
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        for (Module module : manager.getModules()) {
            int key = module.getKeyBind();
            if (key <= 0) continue;
            boolean pressed = InputConstants.isKeyDown(key);
            boolean wasPressedBefore = prevKeyState.getOrDefault(key, false);
            if (pressed && !wasPressedBefore) {
                manager.onKeyPress(key);
            }
            prevKeyState.put(key, pressed);
        }
    }

    @Inject(method={"close"}, at={@At(value="HEAD")})
    private void onClose(CallbackInfo ci) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager != null) {
            manager.save();
            NoxMenuMod.LOGGER.info("NoxMenu config saved on shutdown");
        }
    }
}

