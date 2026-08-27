/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class FreecamNoclip
extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.5, 5.0, 0.1);
    private boolean wasFlying = false;

    public FreecamNoclip() {
        super("FreecamNoclip", "Camara libre que permite atravesar bloques. | Free-cam that also lets you phase through blocks.", Category.MOVEMENT);
        this.addSetting(this.speed);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) {
            return;
        }
        this.wasFlying = FreecamNoclip.mc.player.getAbilities().flying;
        FreecamNoclip.mc.player.noPhysics = true;
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) {
            return;
        }
        FreecamNoclip.mc.player.noPhysics = false;
        FreecamNoclip.mc.player.getAbilities().flying = this.wasFlying;
        FreecamNoclip.mc.player.getAbilities().setFlyingSpeed(0.05f);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        FreecamNoclip.mc.player.noPhysics = true;
        FreecamNoclip.mc.player.getAbilities().flying = true;
        FreecamNoclip.mc.player.getAbilities().setFlyingSpeed((float)((double)0.05f * (Double)this.speed.getValue()));
        FreecamNoclip.mc.player.fallDistance = 0.0;
    }
}

