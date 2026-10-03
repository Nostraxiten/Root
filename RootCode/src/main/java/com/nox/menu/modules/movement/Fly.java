/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class Fly
extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.5, 5.0, 0.1);
    private boolean wasFlying = false;

    public Fly() {
        super("Fly", "Permite vuelo libre en modo supervivencia. | Allows free flight in survival mode.", Category.MOVEMENT);
        this.addSetting(this.speed);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) {
            return;
        }
        this.wasFlying = Fly.mc.player.getAbilities().flying;
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) {
            return;
        }
        Fly.mc.player.getAbilities().flying = this.wasFlying;
        Fly.mc.player.getAbilities().setFlyingSpeed(0.05f);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        float targetSpeed = (float)(0.05 * (Double)this.speed.getValue());
        if (!Fly.mc.player.getAbilities().flying) {
            Fly.mc.player.getAbilities().flying = true;
        }
        if (Fly.mc.player.getAbilities().getFlyingSpeed() != targetSpeed) {
            Fly.mc.player.getAbilities().setFlyingSpeed(targetSpeed);
        }
    }
}