package com.nox.menu.mixin;

import com.nox.menu.modules.render.XRay;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Block.class})
public abstract class MixinBlock {
    @Inject(method = {"shouldRenderFace"}, at = {@At(value = "HEAD")}, cancellable = true)
    private static void onShouldRenderFace(BlockState state, BlockState stateFrom, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (XRay.isXRayActive()) {
            boolean thisTarget = XRay.shouldRenderBlock(state.getBlock());
            boolean neighborTarget = XRay.shouldRenderBlock(stateFrom.getBlock());
            if (thisTarget && neighborTarget) {
                // Both sides are XRay targets (e.g. a dense cluster of ores/spawners/chests):
                // let normal occlusion decide instead of force-drawing every internal face,
                // otherwise a dense structure can emit millions of hidden faces and overflow
                // the BufferBuilder vertex limit (>16777215) during chunk mesh building.
                return;
            }
            cir.setReturnValue(thisTarget);
        }
    }
}
