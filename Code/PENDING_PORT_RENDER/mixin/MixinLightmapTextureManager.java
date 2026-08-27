package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.world.NightVision;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * NightVision — fuerza el factor de vision nocturna del lightmap.
 *
 * En 1.21.11, LightmapTextureManager.update(float) construye el UBO del
 * lightmap con Std140Builder en vez de pasar uniforms sueltos a un shader.
 * El "NightVisionFactor" ya no es una variable local aislada facil de
 * localizar por indice/slot (el metodo fue reescrito); en su lugar es el
 * 4o argumento (ordinal 3) de las 7 llamadas encadenadas a
 * Std140Builder.putFloat(F) dentro de ese metodo:
 *   putFloat(f) putFloat(g) putFloat(n) putFloat(m) putFloat(k) putFloat(skyDarkness) putFloat(gammaAdj)
 * donde "m" es exactamente el valor NightVisionFactor original.
 *
 * Usar @ModifyArg sobre esa invocacion es mas estable que apuntar a un
 * indice de variable local, porque no depende de como el decompilador o
 * javac numeren los slots: solo depende del orden real de las llamadas
 * putFloat(F) en el bytecode, que es estructural al metodo.
 *
 * Igual que el hook original de 1.21.5: si el potion real de Night Vision
 * o Conduit Power ya dan un valor mayor, Math.max evita pisarlo hacia abajo.
 */
@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {

    @ModifyArg(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;",
            ordinal = 3
        )
    )
    private float noxMenu$modifyNightVisionFactor(float original) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return original;

        NightVision module = manager.getModule(NightVision.class);
        if (module == null || !module.isEnabled()) return original;

        return Math.max(original, module.getStrength());
    }
}
