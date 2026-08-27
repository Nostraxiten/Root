package com.nox.menu.mixin;

import com.nox.menu.modules.render.XRay;
import com.nox.menu.modules.optimize.LeavesOptimizer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockBehaviour.BlockBehaviourState.class})
public abstract class MixinBlockBehaviourState {
    @Shadow
    public abstract Block getBlock();

    @Inject(method={"getRenderType"}, at={@At(value="HEAD")}, cancellable=true)
    private void onGetRenderType(CallbackInfoReturnable<RenderShape> cir) {
        if (XRay.isXRayActive() && !XRay.shouldRenderBlock(this.getBlock())) {
            cir.setReturnValue(RenderShape.INVISIBLE);
            return;
        }
        if (LeavesOptimizer.isLeavesOptimizerEnabled() && isTargetGrass(this.getBlock())) {
            cir.setReturnValue(RenderShape.INVISIBLE);
        }
    }

    @Inject(method={"isOpaque"}, at={@At(value="HEAD")}, cancellable=true)
    private void onIsOpaque(CallbackInfoReturnable<Boolean> cir) {
        if (XRay.isXRayActive()) {
            cir.setReturnValue(false);
            return;
        }
        if (LeavesOptimizer.isLeavesOptimizerEnabled() && this.getBlock() instanceof LeavesBlock) {
            if (!LeavesOptimizer.isKeepShape()) {
                cir.setReturnValue(true);
            }
        }
    }

    private static boolean isTargetGrass(Block block) {
        return block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS
            || block == Blocks.FERN || block == Blocks.LARGE_FERN;
    }
}