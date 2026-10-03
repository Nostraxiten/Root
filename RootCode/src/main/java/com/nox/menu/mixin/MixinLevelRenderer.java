package com.nox.menu.mixin;

import com.nox.menu.modules.render.ChunkOptimizer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** ChunkOptimizer — resets the per-frame rebuild counter at the start of each frame. */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(CallbackInfo ci) {
        ChunkOptimizer.onFrameStart();
    }
}
