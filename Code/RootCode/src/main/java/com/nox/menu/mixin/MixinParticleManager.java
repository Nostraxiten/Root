package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.NoParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ParticleEngine.class})
public abstract class MixinParticleManager {
    @Inject(method = "add", at = @At(value = "HEAD"), cancellable = true)
    private void onAdd(Particle particle, CallbackInfo ci) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        NoParticles noParticles = manager.getModule(NoParticles.class);
        if (noParticles != null && noParticles.isEnabled()) {
            ci.cancel();
        }
    }
}
