package com.nox.menu.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.NoFog;
import com.nox.menu.modules.render.XRay;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NoFog / XRay — suprime la niebla del mundo.
 *
 * En Yarn 1.21.11, BackgroundRenderer fue renombrado a FogRenderer y la
 * pipeline de niebla se reescribio: applyFog(...) ahora sube un UBO a la
 * GPU en lugar de devolver un objeto Fog. En vez de interceptar ese calculo
 * (que ya no retorna nada util para cancelar), el hook correcto es
 * getFogBuffer(FogType), que decide que buffer de niebla usa el frame
 * actual. FogRenderer ya trae un "emptyBuffer" precalculado sin niebla
 * (usado para FogType.NONE); cuando NoFog o XRay estan activos devolvemos
 * ese mismo buffer sin niebla independientemente del FogType pedido.
 */
@Mixin(FogRenderer.class)
public abstract class MixinBackgroundRenderer {

    @Shadow
    @Final
    private GpuBuffer emptyBuffer;

    @Inject(method = "getFogBuffer", at = @At("HEAD"), cancellable = true)
    private void noxMenu$onGetFogBuffer(FogRenderer.FogType fogType, CallbackInfoReturnable<GpuBufferSlice> cir) {
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
