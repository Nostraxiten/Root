/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.projectile.FishingHook
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.FishingRodItem
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;

public class AutoFish
extends Module {
    private int recastTimer = -1;

    public AutoFish() {
        super("AutoFish", "Recoge y lanza automaticamente la cana de pescar. | Automatically reels and re-casts the fishing rod.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        FishingHook bobber;
        if (!this.nullCheck()) {
            return;
        }
        if (this.recastTimer > 0) {
            --this.recastTimer;
            if (this.recastTimer == 0) {
                AutoFish.mc.gameMode.useItem((Player)AutoFish.mc.player, InteractionHand.MAIN_HAND);
            }
            return;
        }
        if (AutoFish.mc.player.getMainHandItem().getItem() instanceof FishingRodItem && (bobber = AutoFish.mc.player.fishing) != null) {
            boolean inWater = bobber.isInWater();
            double velY = bobber.getDeltaMovement().y;
            double velX = Math.abs(bobber.getDeltaMovement().x);
            double velZ = Math.abs(bobber.getDeltaMovement().z);
            if (inWater && velY < -0.04 && velX < 0.05 && velZ < 0.05) {
                AutoFish.mc.gameMode.useItem((Player)AutoFish.mc.player, InteractionHand.MAIN_HAND);
                this.recastTimer = 15;
            }
        }
    }
}

