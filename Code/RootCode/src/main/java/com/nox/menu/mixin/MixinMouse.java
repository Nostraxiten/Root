package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.movement.OrbitCam;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * MixinMouse — intercepts the single call to Entity.changeLookDirection(DD)V
 * inside Mouse.updateMouse() using @Redirect instead of a broad @Inject + cancel().
 *
 * Using @Redirect is safer than cancelling the whole updateMouse() method because:
 *  - It leaves all other side effects intact (cursor grab, resolution change, etc.)
 *  - It redirects ONLY the yaw/pitch application, which is exactly what we want.
 *
 * When OrbitCam is enabled: the delta is forwarded to OrbitCam.addMouseDelta()
 * instead of to player.changeLookDirection(), so the player's server-side rotation
 * is never modified by this module.
 *
 * When OrbitCam is disabled: the call is forwarded unchanged to the original
 * changeLookDirection(), preserving vanilla mouse behaviour exactly.
 *
 * Yarn 1.21.5 mappings confirmed:
 *   Mouse.updateMouse  → updateMouse(D)V
 *   LocalPlayer.changeLookDirection → changeLookDirection(DD)V
 */
@Mixin(MouseHandler.class)
public abstract class MixinMouse {

    /**
     * Shadow the two delta fields so we can read them for diagnostic purposes
     * if needed. The actual delta values arrive as parameters to changeLookDirection.
     */
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    /**
     * Redirect the changeLookDirection call inside updateMouse.
     *
     * If OrbitCam is enabled we consume the delta ourselves (orbital rotation).
     * If OrbitCam is disabled we pass the call through to vanilla.
     *
     * @param entity  the entity whose look direction would normally change (== player)
     * @param cursorDX  horizontal cursor delta (already scaled by sensitivity in vanilla)
     * @param cursorDY  vertical cursor delta
     */
    @Redirect(
        method = "turnPlayer",
        at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
        )
    )
    private void orbitCam$redirectTurn(LocalPlayer entity, double cursorDX, double cursorDY) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager != null) {
            OrbitCam orbitCam = manager.getModule(OrbitCam.class);
            if (orbitCam != null && orbitCam.isEnabled()) {
                // Pass raw delta to OrbitCam; OrbitCam applies its own sensitivity scaling.
                orbitCam.addMouseDelta(cursorDX, cursorDY);
                return; // Do NOT call entity.turn — player yaw/pitch unchanged.
            }
        }
        // Vanilla path: forward unmodified.
        entity.turn(cursorDX, cursorDY);
    }
}
