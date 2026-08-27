package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FPSBoost.NoWeather — cancela el renderizado de lluvia/nieve. */
@Mixin(WeatherEffectRenderer.class)
public abstract class MixinWeatherEffectRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(Vec3 camPos, WeatherRenderState renderState, CallbackInfo ci) {
        if (FPSBoost.shouldDisableWeather()) {
            ci.cancel();
        }
    }
}
