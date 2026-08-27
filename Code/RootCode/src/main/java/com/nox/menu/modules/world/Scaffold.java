/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;

public class Scaffold
extends Module {
    public Scaffold() {
        super("Scaffold", "Coloca bloques bajo tus pies al caminar. | Places blocks under your feet as you walk.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        BlockPos posUnder = Scaffold.mc.player.blockPosition().below();
        if (Scaffold.mc.level.getBlockState(posUnder).canBeReplaced()) {
            if (!(Scaffold.mc.player.getMainHandItem().getItem() instanceof BlockItem)) {
                return;
            }
            Vec3 hitVec = new Vec3((double)posUnder.getX() + 0.5, (double)posUnder.getY() + 1.0, (double)posUnder.getZ() + 0.5);
            BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, posUnder, false);
            Scaffold.mc.gameMode.useItemOn(Scaffold.mc.player, InteractionHand.MAIN_HAND, hitResult);
            Scaffold.mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}

