/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;

public class AutoMine
extends Module {
    public AutoMine() {
        super("AutoMine", "Mantiene pulsado el boton de romper automaticamente. | Keeps the break button held automatically.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (AutoMine.mc.hitResult != null && AutoMine.mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult)AutoMine.mc.hitResult;
            AutoMine.mc.gameMode.continueDestroyBlock(hitResult.getBlockPos(), hitResult.getDirection());
            AutoMine.mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}

