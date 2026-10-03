package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FPSBoost.NoClouds — cancela el renderizado de nubes. */
@Mixin(CloudRenderer.class)
public abstract class MixinCloudRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(CallbackInfo ci) {
        if (FPSBoost.shouldDisableClouds()) {
            ci.cancel();
        }
    }
}
