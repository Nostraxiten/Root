/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.client.gui.DrawContext
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.DrawContext;

public class HUDOverlay
extends Module {
    private final BooleanSetting showCoords = new BooleanSetting("Coordinates", true);
    private final BooleanSetting showFps = new BooleanSetting("FPS", true);
    private final BooleanSetting showBiome = new BooleanSetting("Biome", false);

    public HUDOverlay() {
        super("HUDOverlay", "Muestra info de superposicion en la pantalla. | Shows coords, FPS, TPS and active modules on-screen.", Category.PLAYER);
        this.addSetting(this.showCoords);
        this.addSetting(this.showFps);
        this.addSetting(this.showBiome);
        this.setEnabled(true);
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        BlockPos pos;
        if (!this.nullCheck()) {
            return;
        }
        int y = 5;
        int color = -1;
        int padding = 10;
        if (((Boolean)this.showFps.getValue()).booleanValue()) {
            String fps = "FPS: " + mc.getCurrentFps();
            context.drawTextWithShadow(HUDOverlay.mc.textRenderer, fps, padding, y, color);
            Objects.requireNonNull(HUDOverlay.mc.textRenderer);
            y += 11;
        }
        if (((Boolean)this.showCoords.getValue()).booleanValue()) {
            pos = HUDOverlay.mc.player.getBlockPos();
            String coords = String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ());
            context.drawTextWithShadow(HUDOverlay.mc.textRenderer, coords, padding, y, color);
            Objects.requireNonNull(HUDOverlay.mc.textRenderer);
            y += 11;
        }
        if (((Boolean)this.showBiome.getValue()).booleanValue()) {
            pos = HUDOverlay.mc.player.getBlockPos();
            String biomeId = HUDOverlay.mc.world.getBiome(pos).getKey().map(k -> k.getValue().getPath()).orElse("Unknown");
            String biome = "Biome: " + biomeId;
            context.drawTextWithShadow(HUDOverlay.mc.textRenderer, biome, padding, y, color);
            Objects.requireNonNull(HUDOverlay.mc.textRenderer);
            y += 11;
        }
    }
}

