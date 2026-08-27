package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.render.ChunkOptimizer;
import com.nox.menu.modules.render.CustomOutline;
import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * En 1.21.11, WorldRenderer fue reescrito por completo alrededor de
 * FrameGraphBuilder (render pases diferidos en lugar de dibujo inmediato).
 * render()/renderWeather()/renderClouds()/drawBlockOutline() siguen
 * existiendo con esos mismos nombres, asi que los hooks HEAD/cancellable
 * de 1.21.5 se pueden restaurar tal cual: al cancelar renderWeather /
 * renderClouds en HEAD, el pase correspondiente nunca se registra en el
 * FrameGraphBuilder, con el mismo efecto que cancelarlo antes.
 *
 * drawBlockOutline cambio de firma: ya no recibe Entity/BlockPos/BlockState
 * sueltos (van empaquetados en OutlineRenderState) pero ahora SI recibe el
 * "int color" y el "float lineWidth" como parametros directos del metodo,
 * asi que CustomOutline ya no necesita tocar RenderSystem.lineWidth() (que
 * fue eliminado de RenderSystem en 1.21.11): basta con modificar esos dos
 * argumentos via @ModifyVariable(argsOnly = true).
 */
@Mixin(value = {WorldRenderer.class})
public abstract class MixinWorldRenderer {

    @Inject(method = {"render"}, at = {@At(value = "HEAD")})
    private void onRenderStart(CallbackInfo ci) {
        ChunkOptimizer.onFrameStart();
    }

    @Inject(method = {"renderWeather"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onRenderWeather(CallbackInfo ci) {
        if (FPSBoost.shouldDisableWeather()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderClouds"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onRenderClouds(CallbackInfo ci) {
        if (FPSBoost.shouldDisableClouds()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int modifyOutlineColor(int originalColor) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return originalColor;

        CustomOutline module = manager.getModule(CustomOutline.class);
        if (module != null && module.isEnabled()) {
            return module.getColor();
        }
        return originalColor;
    }

    @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyOutlineWidth(float originalWidth) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return originalWidth;

        CustomOutline module = manager.getModule(CustomOutline.class);
        if (module != null && module.isEnabled()) {
            return module.getWidth();
        }
        return originalWidth;
    }
}
