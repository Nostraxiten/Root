package com.nox.menu.mixin;

import com.nox.menu.modules.optimize.LeavesOptimizer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class MixinBlockColors {
    
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void onGetColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (LeavesOptimizer.isLeavesOptimizerEnabled() && state.getBlock() instanceof LeavesBlock) {
            cir.setReturnValue(LeavesOptimizer.getLeafColor());
        }
    }
}
