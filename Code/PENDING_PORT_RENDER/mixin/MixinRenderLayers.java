package com.nox.menu.mixin;

import com.nox.menu.modules.optimize.LeavesOptimizer;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.BlockRenderLayers.class)
public class MixinRenderLayers {

    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void onGetBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (LeavesOptimizer.isLeavesOptimizerEnabled()
                && state.getBlock() instanceof LeavesBlock
                && !LeavesOptimizer.isKeepShape()) {
            cir.setReturnValue(BlockRenderLayer.SOLID);
        }
    }
}