package com.nox.menu.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.NoFog;
import com.nox.menu.modules.render.XRay;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NoFog / XRay — suprime la niebla del mundo.
 *
 * En 26.2, FogRenderer.getFogBuffer(FogType) fue reemplazado por
 * getBuffer(FogMode). El emptyBuffer precalculado sin niebla sigue existiendo
 * (usado para FogMode.NONE); cuando NoFog o XRay estan activos devolvemos
 * ese mismo buffer sin niebla independientemente del FogMode pedido.
 */
@Mixin(FogRenderer.class)
public abstract class MixinBackgroundRenderer {

    @Shadow
    @Final
    private GpuBuffer emptyBuffer;

    @Inject(method = "getBuffer", at = @At("HEAD"), cancellable = true)
    private void noxMenu$onGetBuffer(FogRenderer.FogMode fogMode, CallbackInfoReturnable<GpuBufferSlice> cir) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;

        NoFog noFog = manager.getModule(NoFog.class);
        boolean noFogActive = noFog != null && noFog.isEnabled();

        XRay xray = manager.getModule(XRay.class);
        boolean xrayActive = xray != null && xray.isEnabled();

        if (noFogActive || xrayActive) {
            cir.setReturnValue(this.emptyBuffer.slice(0L, FogRenderer.FOG_UBO_SIZE));
        }
    }
}
