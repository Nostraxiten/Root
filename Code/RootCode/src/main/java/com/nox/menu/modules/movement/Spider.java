/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.client.player.LocalPlayer
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.player.LocalPlayer;

public class Spider
extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.2, 0.1, 1.0, 0.1);

    public Spider() {
        super("Spider", "Permite escalar cualquier pared como una escalera. | Lets you climb any wall like a ladder.", Category.MOVEMENT);
        this.addSetting(this.speed);
    }

    public void applyClimb(LocalPlayer player) {
        if (player.horizontalCollision && player.input.keyPresses.forward()) {
            Vec3 velocity = player.getDeltaMovement();
            player.setDeltaMovement(velocity.x, ((Double)this.speed.getValue()).doubleValue(), velocity.z);
        }
    }
}

