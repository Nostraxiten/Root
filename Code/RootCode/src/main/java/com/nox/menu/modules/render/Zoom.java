/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class Zoom
extends Module {
    private final NumberSetting factor = new NumberSetting("Factor", 4.0, 1.0, 10.0, 0.5);
    private boolean zooming = false;

    public Zoom() {
        super("Zoom", "Hace zoom con la camara suavemente. | Zooms in the camera smoothly.", Category.RENDER);
        this.addSetting(this.factor);
    }

    @Override
    public void onTick() {
        this.zooming = true;
    }

    @Override
    public void onDisable() {
        this.zooming = false;
    }

    public boolean isZooming() {
        return this.zooming;
    }

    public float getZoomFactor() {
        return (float)((Double)this.factor.getValue()).doubleValue();
    }
}

