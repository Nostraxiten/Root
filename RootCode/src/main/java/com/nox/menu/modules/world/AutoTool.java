/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AutoTool
extends Module {
    public AutoTool() {
        super("AutoTool", "Cambia automaticamente a la mejor herramienta para el bloque. | Auto-switches to the best tool for the block.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (AutoTool.mc.options.keyAttack.isDown() && AutoTool.mc.hitResult != null && AutoTool.mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult)AutoTool.mc.hitResult;
            BlockState state = AutoTool.mc.level.getBlockState(hitResult.getBlockPos());
            float bestSpeed = 1.0f;
            int bestSlot = -1;
            for (int i = 0; i < 9; ++i) {
                float speed;
                ItemStack stack = AutoTool.mc.player.getInventory().getItem(i);
                if (stack.isEmpty() || !((speed = stack.getDestroySpeed(state)) > bestSpeed)) continue;
                bestSpeed = speed;
                bestSlot = i;
            }
            if (bestSlot != -1 && AutoTool.mc.player.getInventory().getSelectedSlot() != bestSlot) {
                AutoTool.mc.player.getInventory().setSelectedSlot(bestSlot);
            }
        }
    }
}

