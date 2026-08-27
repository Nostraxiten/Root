/*
 * Decompiled with CFR 0.152.
 */
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
        return INSTANCE != null && (Boolean)LeavesOptimizer.INSTANCE.keepShape.getValue() != false;
    }

    public static int getLeafColor() {
        return INSTANCE != null ? (Integer)LeavesOptimizer.INSTANCE.leafColor.getValue() : 3066944;
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
        if (LeavesOptimizer.mc.levelRenderer != null && LeavesOptimizer.mc.level != null) {
            LeavesOptimizer.mc.levelRenderer.reload();
        }
    }
}

