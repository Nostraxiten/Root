package com.nox.menu.mixin;

import com.nox.menu.modules.render.FPSBoost;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * LimitEntities / EntityDist — descarta el render de entidades más allá de
 * una distancia configurable al jugador local.
 *
 * En 1.21.11, EntityRenderDispatcher fue renombrado a EntityRenderManager,
 * pero shouldRender(E, Frustum, double, double, double) conserva exactamente
 * la misma firma (verificado contra las fuentes decompiladas de esta
 * version), asi que el hook original se restaura sin cambios y sin
 * require=0 defensivo.
 *
 * Usa entity.getX()/getY()/getZ() en lugar de entity.getPos() para
 * compatibilidad con Yarn 1.21.11 donde getPos() fue renombrado.
 */
@Mixin(EntityRenderManager.class)
public class MixinEntityRenderDispatcher {

    @Inject(
        method = "shouldRender",
        at = @At("HEAD"),
        cancellable = true
    )
    private <E extends Entity> void onShouldRender(
            E entity,
            Frustum frustum,
            double cameraX,
            double cameraY,
            double cameraZ,
            CallbackInfoReturnable<Boolean> cir) {
        if (!FPSBoost.shouldLimitEntityDist()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (entity == client.player) return;

        double maxDist = FPSBoost.getEntityRenderDist();
        double maxDistSq = maxDist * maxDist;

        double dx = entity.getX() - cameraX;
        double dy = entity.getY() - cameraY;
        double dz = entity.getZ() - cameraZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > maxDistSq) {
            cir.setReturnValue(false);
        }
    }
}
