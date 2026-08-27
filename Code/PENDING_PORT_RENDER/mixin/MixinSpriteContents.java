package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AnimationThrottle — omite ticks de animación de textura para ahorrar CPU.
 *
 * Target: SpriteContents$Animator (1.21.11+)
 * Método: tick()V  — sin parámetros (cambio de API respecto a 1.21.5 que usaba tick(IILGpuTexture;)V)
 *
 * NOTA: En versiones anteriores existía SpriteContents$AnimatorImpl con
 *   tick(int x, int y, GpuTexture texture). En 1.21.11 la clase interna
 *   fue simplificada a SpriteContents$Animator con tick() sin argumentos.
 */
@Mixin(targets = "net.minecraft.client.texture.SpriteContents$Animator")
public abstract class MixinSpriteContents {

    @Inject(
        method = "tick()V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onTick(CallbackInfo ci) {
        if (FPSBoost.shouldSkipAnimationTick()) {
            ci.cancel();
        }
    }
}
