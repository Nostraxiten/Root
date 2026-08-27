package com.nox.menu.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.nox.menu.modules.render.ChunkOptimizer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** ChunkOptimizer — resets the per-frame rebuild counter at the start of each frame. */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderBlockOutline,
                                CameraRenderState cameraRenderState, Matrix4fc frustumMatrix, GpuBufferSlice fogBuffer,
                                Vector4f clearColor, boolean skipMainTarget, CallbackInfo ci) {
        ChunkOptimizer.onFrameStart();
    }
}
