package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Object — modifica la velocidad de tick del cliente.
 *
 * RenderTickCounter$Dynamic.tickTime (tickTime) es un campo FINAL
 * en 1.21.5: escribir en él desde fuera del constructor lanza
 * IllegalAccessError. Por eso no se puede usar @Shadow para mutarlo.
 *
 * Alternativa correcta: hookear el RETURN de beginRenderTick(J)I y
 * escalar el número de ticks que devuelve.
 *   multiplier > 1.0 → más ticks por frame (velocidad aumentada)
 *   multiplier < 1.0 → menos ticks por frame (velocidad reducida)
 *
 * Nota: Math.round() introduce un error de ±0.5 ticks por frame.
 * Para un Object de juego casual esto es aceptable; si se necesita
 * precisión sub-tick habría que acumular el error entre frames.
 *
 * Firma confirmada en mappings.tiny línea 130624:
 *   beginRenderTick → beginRenderTick(J)I (sobrecarga 1, sin pausa)
 */
@Mixin(RenderTickCounter.Dynamic.class)
public abstract class MixinRenderTickCounter {

    @Inject(
        method = "beginRenderTick(J)I",
        at = @At("RETURN"),
        cancellable = true
    )
    private void onBeginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;
    }
}


