/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.phys.Vec3;

public class Speed
extends Module {
    private final NumberSetting groundMultiplier = new NumberSetting("Ground Multiplier", 1.3, 1.0, 3.0, 0.05);
    private final NumberSetting airMultiplier = new NumberSetting("Air Multiplier", 1.15, 1.0, 2.0, 0.05);

    public Speed() {
        super("Speed", "Aumenta tu velocidad de movimiento. | Increases your movement speed.", Category.MOVEMENT);
        this.addSetting(this.groundMultiplier);
        this.addSetting(this.airMultiplier);
    }

    @Override
    public void onTick() {
        boolean moving;
        if (!this.nullCheck()) {
            return;
        }
        boolean bl = moving = Speed.mc.player.input.keyPresses.forward() || Speed.mc.player.input.keyPresses.backward() || Speed.mc.player.input.keyPresses.left() || Speed.mc.player.input.keyPresses.right();
        if (!moving) {
            return;
        }
        Vec3 vel = Speed.mc.player.getDeltaMovement();
        double multiplier = Speed.mc.player.onGround() ? (Double)this.groundMultiplier.getValue() : (Double)this.airMultiplier.getValue();
        Speed.mc.player.setDeltaMovement(vel.x * multiplier, vel.y, vel.z * multiplier);
    }
}

