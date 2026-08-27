package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * LowFire — reduce el overlay de fuego en primera persona escalando
 * la MatrixStack antes de que renderFireOverlay dibuje los quads.
 *
 * Hook: InGameOverlayRenderer#renderFireOverlay(MatrixStack, VertexConsumerProvider)
 * Confirmado en Yarn 1.21.5+build.1 (mappings.tiny línea 155529,
 * método_23070, clase gsa / net.minecraft.client.gui.hud.InGameOverlayRenderer).
 *
 * Estrategia: @Inject en HEAD + scale(). La escala < 1 reduce el overlay
 * visualmente sin tocar el resto del HUD (crosshair, hotbar, etc.).
 * El MatrixStack.push/pop es necesario para no contaminar el contexto de render.
 * Se hace push aquí y pop en TAIL del mismo método.
 */
@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

    /**
     * Aplica scale < 1 antes de que se dibujen los quads del fuego.
     * Solo actúa si FPSBoost está activo y LowFire habilitado.
     */
    @Inject(
        method = "renderFireOverlay",
        at = @At("HEAD")
    )
    private static void onRenderFireOverlay_head(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Sprite sprite,
            CallbackInfo ci) {
        if (!FPSBoost.shouldLowFire()) return;

        float scale = FPSBoost.getFireScale();
        matrices.push();
        // Escalar centrado: el overlay cubre [-1,1] en x/y; scale < 1 lo reduce.
        matrices.scale(scale, scale, 1.0f);
    }

    /**
     * Restaura la MatrixStack después de renderizar el overlay de fuego.
     */
    @Inject(
        method = "renderFireOverlay",
        at = @At("TAIL")
    )
    private static void onRenderFireOverlay_tail(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Sprite sprite,
            CallbackInfo ci) {
        if (!FPSBoost.shouldLowFire()) return;
        matrices.pop();
    }
}
