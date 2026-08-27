package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.world.NightVision;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NightVision — fuerza el factor de vision nocturna del lightmap.
 *
 * 26.2 movio el calculo del lightmap a un extractor de render-state dedicado
 * (LightmapRenderStateExtractor.extract), que escribe directamente el campo
 * publico LightmapRenderState.nightVisionEffectIntensity (ya no hay uniforms
 * sueltos ni Std140Builder.putFloat encadenados que localizar por ordinal).
 * Esto es mas estable que el enfoque anterior: solo depende de un campo
 * publico con nombre estable, no de la posicion de una llamada en bytecode.
 *
 * Igual que antes: si el potion real de Night Vision o Conduit Power ya dan
 * un valor mayor, Math.max evita pisarlo hacia abajo.
 */
@Mixin(LightmapRenderStateExtractor.class)
public abstract class MixinLightmapTextureManager {

    @Inject(method = "extract", at = @At("TAIL"))
    private void noxMenu$modifyNightVisionFactor(LightmapRenderState renderState, float partialTicks, CallbackInfo ci) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;

        NightVision module = manager.getModule(NightVision.class);
        if (module == null || !module.isEnabled()) return;

        renderState.nightVisionEffectIntensity = Math.max(renderState.nightVisionEffectIntensity, module.getStrength());
    }
}
