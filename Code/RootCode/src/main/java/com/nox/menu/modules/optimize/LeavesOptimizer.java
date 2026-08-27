package com.nox.menu.modules.optimize;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;

public class LeavesOptimizer
extends Module {
    private final ColorSetting leafColor = new ColorSetting("leafColor", 3066944);
    private final BooleanSetting keepShape = new BooleanSetting("keepShape", false);
    private static LeavesOptimizer INSTANCE;

    public LeavesOptimizer() {
        super("Optimize", "Optimiza hojas y hierba para mejor rendimiento | Optimizes leaves and grass for better performance", Category.OPTIMIZE);
        this.addSetting(this.leafColor);
        this.addSetting(this.keepShape);
        this.setEnabled(false);
        INSTANCE = this;
    }

    public static boolean isLeavesOptimizerEnabled() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    public static boolean isKeepShape() {
        return INSTANCE != null && LeavesOptimizer.INSTANCE.keepShape.getValue();
    }

    public static int getLeafColor() {
        return INSTANCE != null ? LeavesOptimizer.INSTANCE.leafColor.getValue() : 3066944;
    }

    @Override
    public void onEnable() {
        this.reloadWorldRenderer();
    }

    @Override
    public void onDisable() {
        this.reloadWorldRenderer();
    }

    private void reloadWorldRenderer() {
        if (LeavesOptimizer.mc.level == null || LeavesOptimizer.mc.player == null) {
            return;
        }
        int cx = LeavesOptimizer.mc.player.chunkPosition().x();
        int cz = LeavesOptimizer.mc.player.chunkPosition().z();
        int r = LeavesOptimizer.mc.options.getEffectiveRenderDistance() + 1;
        int minSY = LeavesOptimizer.mc.level.getMinSectionY();
        int maxSY = LeavesOptimizer.mc.level.getMaxSectionY();
        LeavesOptimizer.mc.level.setSectionRangeDirty(cx - r, minSY, cz - r, cx + r, maxSY, cz + r);
    }
}
