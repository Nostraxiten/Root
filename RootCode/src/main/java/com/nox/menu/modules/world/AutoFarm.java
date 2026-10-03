/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AutoFarm
extends Module {
    public AutoFarm() {
        super("AutoFarm", "Cosecha cultivos maduros cercanos automaticamente | Harvests fully grown crops nearby automatically", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        BlockPos playerPos = AutoFarm.mc.player.blockPosition();
        int r = 4;
        for (int x = -r; x <= r; ++x) {
            for (int y = -r; y <= r; ++y) {
                for (int z = -r; z <= r; ++z) {
                    CropBlock crop;
                    BlockState state;
                    Block class_22482;
                    BlockPos target = playerPos.offset(x, y, z);
                    if (!AutoFarm.mc.level.hasChunk(target.getX() >> 4, target.getZ() >> 4) || !((class_22482 = (state = AutoFarm.mc.level.getBlockState(target)).getBlock()) instanceof CropBlock) || !(crop = (CropBlock)class_22482).isMaxAge(state)) continue;
                    AutoFarm.mc.gameMode.startDestroyBlock(target, Direction.UP);
                    AutoFarm.mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
                    return;
                }
            }
        }
    }
}

