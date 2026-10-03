package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.movement.NoclipTP;
import com.nox.menu.modules.movement.OrbitCam;
import com.nox.menu.modules.render.Freecam;
import com.nox.menu.modules.render.Zoom;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * MixinCamera — hooks Camera.calculateFov() (Zoom) and Camera.update() to allow
 * render-only camera overrides.
 *
 * Priority order (mutually exclusive), matching 1.21.11:
 *   1. Freecam  (render/Freecam.java) — fully detached flying camera
 *   2. NoclipTP (movement/NoclipTP)   — teleport-preview camera
 *   3. OrbitCam (movement/OrbitCam)   — orbital third-person camera
 *
 * Only the first active module in this list applies its override.
 * OrbitCam NEVER writes player.setYRot/setXRot.
 */
@Mixin(value = {Camera.class})
public abstract class MixinCamera {

    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yRot, float xRot);

    /**
     * Tracks the previous system time (nanoseconds) so we can compute the
     * real frame time in seconds for delta-time-based lerp in OrbitCam.
     */
    private long orbitCam$prevNanos = -1L;

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void onCalculateFov(float partialTicks, CallbackInfoReturnable<Float> cir) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        Zoom zoom = manager.getModule(Zoom.class);
        if (zoom != null && zoom.isEnabled() && zoom.isZooming()) {
            cir.setReturnValue(cir.getReturnValue() / zoom.getZoomFactor());
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(DeltaTracker deltaTracker, CallbackInfo ci) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;

        // ── 1. Freecam ────────────────────────────────────────────────────
        Freecam freecam = manager.getModule(Freecam.class);
        if (freecam != null && freecam.isEnabled()) {
            this.setPosition(freecam.getCamX(), freecam.getCamY(), freecam.getCamZ());
            this.setRotation(freecam.getCamYaw(), freecam.getCamPitch());
            return;
        }

        // ── 2. NoclipTP ───────────────────────────────────────────────────
        NoclipTP noclipTP = manager.getModule(NoclipTP.class);
        if (noclipTP != null && noclipTP.isEnabled()) {
            this.setPosition(noclipTP.getCamX(), noclipTP.getCamY(), noclipTP.getCamZ());
            this.setRotation(noclipTP.getCamYaw(), noclipTP.getCamPitch());
            return;
        }

        // ── 3. OrbitCam ───────────────────────────────────────────────────
        OrbitCam orbitCam = manager.getModule(OrbitCam.class);
        if (orbitCam != null && orbitCam.isEnabled()) {
            long now = System.nanoTime();
            float frameTimeS;
            if (orbitCam$prevNanos < 0L) {
                frameTimeS = 0.016f;
            } else {
                frameTimeS = (float) ((now - orbitCam$prevNanos) * 1e-9);
                if (frameTimeS < 0.001f) frameTimeS = 0.001f;
                if (frameTimeS > 0.200f) frameTimeS = 0.200f;
            }
            orbitCam$prevNanos = now;

            float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(true);
            orbitCam.applyToCamera(
                tickDelta,
                frameTimeS,
                (x, y, z) -> this.setPosition(x, y, z),
                (yaw, pitch) -> this.setRotation(yaw, pitch)
            );
        } else {
            orbitCam$prevNanos = -1L;
        }
    }
}
