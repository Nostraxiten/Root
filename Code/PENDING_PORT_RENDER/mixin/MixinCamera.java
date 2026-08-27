package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.movement.OrbitCam;
import com.nox.menu.modules.render.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinCamera — hooks Camera.update() to allow render-only camera overrides.
 *
 * Priority order (mutually exclusive):
 *   1. Freecam  (render/Freecam.java) — fully detached flying camera
 *   2. NoclipTP (movement/NoclipTP)   — teleport-preview camera
 *   3. OrbitCam (movement/OrbitCam)   — orbital third-person camera
 *
 * Only the first active module in this list applies its override.
 * OrbitCam NEVER writes player.setYaw/setPitch.
 */
@Mixin(value = {Camera.class})
public abstract class MixinCamera {

    @Shadow protected abstract void setPos(double var1, double var3, double var5);
    @Shadow protected abstract void setRotation(float var1, float var2);

    /**
     * Tracks the previous system time (nanoseconds) so we can compute the
     * real frame time in seconds for delta-time-based lerp in OrbitCam.
     */
    private long orbitCam$prevNanos = -1L;

    @Inject(method = {"update"}, at = {@At(value = "TAIL")})
    private void onUpdate(World area, Entity focusedEntity,
                          boolean thirdPerson, boolean inverseView,
                          float tickDelta, CallbackInfo ci) {

        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;

        // ── 1. Freecam ────────────────────────────────────────────────────
        Freecam freecam = manager.getModule(Freecam.class);
        if (freecam != null && freecam.isEnabled()) {
            this.setPos(freecam.getCamX(), freecam.getCamY(), freecam.getCamZ());
            this.setRotation(freecam.getCamYaw(), freecam.getCamPitch());
            return;
        }

        // ── 2. NoclipTP ───────────────────────────────────────────────────
        com.nox.menu.modules.movement.NoclipTP noclipTP =
                manager.getModule(com.nox.menu.modules.movement.NoclipTP.class);
        if (noclipTP != null && noclipTP.isEnabled()) {
            this.setPos(noclipTP.getCamX(), noclipTP.getCamY(), noclipTP.getCamZ());
            this.setRotation(noclipTP.getCamYaw(), noclipTP.getCamPitch());
            return;
        }

        // ── 3. OrbitCam ───────────────────────────────────────────────────
        OrbitCam orbitCam = manager.getModule(OrbitCam.class);
        if (orbitCam != null && orbitCam.isEnabled()) {
            // Compute real frame time in seconds for delta-time lerp.
            // This makes the smoothing feel identical at any frame rate.
            long now = System.nanoTime();
            float frameTimeS;
            if (orbitCam$prevNanos < 0L) {
                frameTimeS = 0.016f; // first frame: assume ~60 fps
            } else {
                frameTimeS = (float)((now - orbitCam$prevNanos) * 1e-9);
                // Clamp to [0.001, 0.2] to guard against pauses / debugger breaks.
                if (frameTimeS < 0.001f) frameTimeS = 0.001f;
                if (frameTimeS > 0.200f) frameTimeS = 0.200f;
            }
            orbitCam$prevNanos = now;

            orbitCam.applyToCamera(
                tickDelta,
                frameTimeS,
                (x, y, z)     -> this.setPos(x, y, z),
                (yaw, pitch)   -> this.setRotation(yaw, pitch)
            );
        } else {
            // Reset timer so a re-enable starts fresh.
            orbitCam$prevNanos = -1L;
        }
    }
}
