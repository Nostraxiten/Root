package com.nox.menu.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;

/**
 * En 1.21.11, lineWidth() fue eliminado de RenderSystem (confirmado:
 * no existe en absoluto en las fuentes decompiladas de esta version).
 * El grosor de linea ahora se pasa explicito como parametro "lineWidth"
 * en WorldRenderer.drawBlockOutline(...), asi que el hook de CustomOutline
 * para el ancho del contorno vive ahora en MixinWorldRenderer en lugar
 * de aqui. Este mixin se mantiene vacio intencionalmente.
 */
@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {
}
