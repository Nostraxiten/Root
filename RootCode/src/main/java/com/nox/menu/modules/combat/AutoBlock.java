/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ShieldItem
 */
package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;

public class AutoBlock
extends Module {
    public AutoBlock() {
        super("AutoBlock", "Bloquea automaticamente con la espada o escudo. | Automatically blocks with sword or shield.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        boolean hasShield = AutoBlock.mc.player.getMainHandItem().getItem() instanceof ShieldItem || AutoBlock.mc.player.getOffhandItem().getItem() instanceof ShieldItem;
        boolean bl = hasShield;
        if (!hasShield) {
            return;
        }
        boolean threatNearby = false;
        for (Entity entity : AutoBlock.mc.level.entitiesForRendering()) {
            if (entity == AutoBlock.mc.player || !(entity instanceof LivingEntity) || !entity.isAlive() || !(AutoBlock.mc.player.distanceTo(entity) < 4.0f) || !(entity instanceof Monster) && !(entity instanceof Player)) continue;
            threatNearby = true;
            break;
        }
        if (threatNearby && !AutoBlock.mc.player.isUsingItem()) {
            InteractionHand hand = AutoBlock.mc.player.getOffhandItem().getItem() instanceof ShieldItem ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            AutoBlock.mc.gameMode.useItem((Player)AutoBlock.mc.player, hand);
        }
    }
}

