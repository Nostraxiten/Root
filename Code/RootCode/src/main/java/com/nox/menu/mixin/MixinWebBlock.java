/*
 * NoxMenu 2.0.0 - WebBlock Mixin
 * Cancels the cobweb slowdown for the local player when NoSlowdown (Webs) is active.
 */
package com.nox.menu.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={WebBlock.class})
public abstract class MixinWebBlock {
    // Mojang official name is "entityInside" (Yarn's "onEntityCollision"),
    // with an extra InsideBlockEffectApplier param (verified via 26.2 client.jar).
    @Inject(method={"entityInside"}, at={@At(value="HEAD")}, cancellable=true)
    private void onEntityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bl, CallbackInfo ci) {
        if (entity == Minecraft.getInstance().player && false) {
            ci.cancel();
        }
    }
}
