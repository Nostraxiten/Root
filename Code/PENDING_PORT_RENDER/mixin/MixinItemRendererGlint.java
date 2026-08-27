package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * FastGlint — reduce el glint de encantamiento de ítems a una sola pasada.
 *
 * Contexto: Vanilla renderiza el glint en dos pasadas superpuestas para
 * crear el efecto de destello. Con FastGlint se devuelve directamente el
 * consumer de la primera pasada (GLINT / ENTITY_GLINT), ignorando la segunda.
 * El resultado es glint visible pero más barato en overdraw.
 *
 * getItemGlintConsumer(VertexConsumerProvider, RenderLayer, boolean, boolean)
 * sigue existiendo sin cambios en 1.21.11 (verificado contra las fuentes
 * decompiladas), asi que el hook de items se restaura tal cual.
 *
 * El hook de armadura (antes getArmorGlintConsumer) ya NO aplica: en
 * 1.21.11 el renderizado de equipo se movió por completo a
 * EquipmentRenderer, que encola sus draws via OrderedRenderCommandQueue en
 * lugar de pedir un VertexConsumer directo, y el glint de armadura ya solo
 * añade una única capa extra (RenderLayers.armorEntityGlint()) en vez del
 * doble-pase que tenía ItemRenderer — no hay ganancia de rendimiento
 * equivalente que restaurar ahí, así que se omite deliberadamente.
 *
 * No colisiona con MixinItemEntityRenderer: ese mixin actúa sobre
 * ItemEntityRenderer#render(), clase distinta a ItemRenderer.
 */
@Mixin(ItemRenderer.class)
public class MixinItemRendererGlint {

    /**
     * Una sola pasada para el glint de ítems (hotbar, suelo, inventario GUI).
     * Solo actúa cuando el parámetro "glint" == true para no tocar
     * ítems sin encantamiento.
     */
    @Inject(
        method = "getItemGlintConsumer",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void onGetItemGlintConsumer(
            VertexConsumerProvider provider,
            RenderLayer layer,
            boolean solid,
            boolean glint,
            CallbackInfoReturnable<VertexConsumer> cir) {
        if (!glint) return;
        if (!FPSBoost.shouldFastGlint()) return;
        // Una sola pasada: devolver el buffer de la primera capa directamente.
        cir.setReturnValue(provider.getBuffer(layer));
    }

}
