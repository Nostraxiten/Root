package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;

public class HUDOverlay
extends Module {
    private final BooleanSetting showCoords = new BooleanSetting("Coordinates", true);
    private final BooleanSetting showFps = new BooleanSetting("FPS", true);
    private final BooleanSetting showBiome = new BooleanSetting("Biome", false);
    private final BooleanSetting showCps = new BooleanSetting("CPS", true);

    // Rolling 1-second window of click timestamps, refilled every render frame
    // (not once per tick) so the counter updates as smoothly/immediately as
    // possible instead of visibly stepping once a tick.
    private final ArrayDeque<Long> clickTimestamps = new ArrayDeque<Long>();
    private boolean wasAttackDown = false;

    public HUDOverlay() {
        super("HUDOverlay", "Muestra info de superposicion en la pantalla. | Shows coords, FPS, TPS and active modules on-screen.", Category.PLAYER);
        this.addSetting(this.showCoords);
        this.addSetting(this.showFps);
        this.addSetting(this.showBiome);
        this.addSetting(this.showCps);
        this.setEnabled(true);
    }

    private int updateAndGetCps() {
        boolean isDown = mc.options.keyAttack.isDown();
        if (isDown && !this.wasAttackDown) {
            this.clickTimestamps.addLast(System.currentTimeMillis());
        }
        this.wasAttackDown = isDown;
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!this.clickTimestamps.isEmpty() && this.clickTimestamps.peekFirst() < cutoff) {
            this.clickTimestamps.pollFirst();
        }
        return this.clickTimestamps.size();
    }

    @Override
    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        int y = 5;
        int color = -1;
        int padding = 10;
        if (this.showFps.getValue()) {
            String fps = "FPS: " + mc.getFps();
            context.text(mc.font, fps, padding, y, color, true);
            y += mc.font.lineHeight;
        }
        if (this.showCps.getValue()) {
            String cps = "CPS: " + this.updateAndGetCps();
            context.text(mc.font, cps, padding, y, color, true);
            y += mc.font.lineHeight;
        }
        if (this.showCoords.getValue()) {
            BlockPos pos = mc.player.blockPosition();
            String coords = String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ());
            context.text(mc.font, coords, padding, y, color, true);
            y += mc.font.lineHeight;
        }
        if (this.showBiome.getValue()) {
            BlockPos pos = mc.player.blockPosition();
            String biomeId = mc.level.getBiomeManager().getBiome(pos).unwrapKey()
                    .map(k -> k.identifier().getPath()).orElse("Unknown");
            String biome = "Biome: " + biomeId;
            context.text(mc.font, biome, padding, y, color, true);
            y += mc.font.lineHeight;
        }
    }
}
