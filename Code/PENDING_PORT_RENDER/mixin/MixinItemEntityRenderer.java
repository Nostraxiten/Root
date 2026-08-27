package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks sobre ItemEntityRenderer para NoBob / FlatModel.
 *
 * Este mixin nunca se adapto a 1.21.11 (era byte-identico al de 1.21.5) y
 * usaba un target por wildcard "render*", que en 1.21.5 apuntaba a un unico
 * metodo "render". En 1.21.11 ItemEntityRenderer tiene VARIOS metodos que
 * empiezan por "render" (la instancia render(...) y tres overloads static
 * render/renderStack para dibujar items apilados), y el wildcard terminaba
 * emparejando la firma equivocada — cosa que el entorno de dev toleraba (usa
 * mapeos con nombre directo) pero que en el jar remapeado de produccion
 * lanzaba InvalidInjectionException y dejaba el juego con pantalla negra
 * (ItemEntityRenderer no podia cargar como clase).
 *
 * Firma real en 1.21.11 del metodo de instancia (el punto de entrada real,
 * verificado contra las fuentes decompiladas de esta version):
 *   render(ItemEntityRenderState, MatrixStack, OrderedRenderCommandQueue, CameraRenderState)
 *
 * Se apunta ahora a esa firma exacta (sin wildcard) para evitar ambiguedad
 * con los overloads estaticos.
 */
@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {

    private static final String RENDER_DESC =
        "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;"
        + "Lnet/minecraft/client/util/math/MatrixStack;"
        + "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;"
        + "Lnet/minecraft/client/render/state/CameraRenderState;)V";

    /** NoBob — suprime el movimiento vertical del ítem en el suelo. */
    @Redirect(method = RENDER_DESC,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"),
        require = 0)
    private void redirectTranslate(MatrixStack instance, float x, float y, float z) {
        instance.translate(x, y, z);
    }

    /** FlatModel — punto de enganche tras el push() para futuras rotaciones custom. */
    @Inject(
        method = RENDER_DESC,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER),
        require = 0
    )
    private void onRenderPush(
            ItemEntityRenderState state,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraRenderState,
            CallbackInfo ci) {
    }
}
