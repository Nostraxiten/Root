package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FPSBoost.NoMenuBlur — cancela el post-proceso de blur (menu de pausa / inventario). */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "processBlurEffect", at = @At("HEAD"), cancellable = true)
    private void onProcessBlurEffect(CallbackInfo ci) {
        if (FPSBoost.shouldDisableMenuBlur()) {
            ci.cancel();
        }
    }
}
