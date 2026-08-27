package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.FPSBoost;
import com.nox.menu.modules.render.Zoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class})
public abstract class MixinGameRenderer {

    @Inject(method={"getFov"}, at={@At(value="RETURN")}, cancellable=true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        Zoom zoom = manager.getModule(Zoom.class);
        if (zoom != null && zoom.isEnabled() && zoom.isZooming()) {
            cir.setReturnValue(Float.valueOf(((Float)cir.getReturnValue()).floatValue() / zoom.getZoomFactor()));
        }
    }

    /**
     * Cancela el post-proceso de blur (menú de pausa / inventario) cuando
     * FPSBoost -> NoMenuBlur está activo.
     * Método: renderBlur()V — Yarn 1.21.5, renderBlur. Sin parámetros.
     */
    @Inject(method = "renderBlur", at = @At("HEAD"), cancellable = true)
    private void onRenderBlur(CallbackInfo ci) {
        if (FPSBoost.shouldDisableMenuBlur()) {
            ci.cancel();
        }
    }
}