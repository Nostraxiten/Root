package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

/**
 * OrbitCam — "F5 desacoplado" orbital camera.
 *
 * The camera POSITION always follows the real player position (just like F5 vanilla).
 * The camera ROTATION (orbitYaw / orbitPitch) is fully independent and only changes
 * via mouse input captured in MixinMouse, multiplied by `sensitivity`.
 *
 * CRITICAL: this module never writes player.setYRot() / setPitch() nor any network
 * rotation packet. It is purely a visual render override. KillAura / CrystalAura
 * manage their own server-side rotation independently and are unaffected.
 */
public class OrbitCam extends Module {

    // ── Settings ──────────────────────────────────────────────────────────
    private final NumberSetting radius      = new NumberSetting("Radius",      4.0, 1.5, 12.0, 0.5);
    private final NumberSetting sensitivity = new NumberSetting("Sensitivity", 1.0, 0.1,  3.0, 0.1);
    private final BooleanSetting collision  = new BooleanSetting("Collision",  true);
    private final BooleanSetting smoothing  = new BooleanSetting("Smoothing",  true);

    // ── Orbital rotation (independent of player.getYaw/getPitch) ──────────
    /** Horizontal angle of the orbital arm, in degrees. */
    private float orbitYaw   = 0f;
    /** Vertical angle of the orbital arm, in degrees. Clamped to [-89, 89] to avoid pole singularities. */
    private float orbitPitch = 0f;

    // ── Smoothed camera position (for delta-time lerp) ────────────────────
    private double smoothX = 0, smoothY = 0, smoothZ = 0;
    private boolean firstFrame = true;

    public OrbitCam() {
        super("OrbitCam", "Camara orbital independiente en 3a persona | Independent orbital 3rd-person camera", Category.MOVEMENT);
        this.addSetting(this.radius);
        this.addSetting(this.sensitivity);
        this.addSetting(this.collision);
        this.addSetting(this.smoothing);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        if (!this.nullCheck()) return;
        // Initialise orbit angles from the current player view so the
        // transition is seamless and no sudden camera jump occurs.
        this.orbitYaw   = mc.player.getYRot();
        this.orbitPitch = mc.player.getXRot();
        this.firstFrame  = true;
    }

    @Override
    public void onDisable() {
        // No cleanup required — MixinCamera stops applying the override as
        // soon as isEnabled() returns false.
        this.firstFrame = true;
    }

    // ── Mouse input (called from MixinMouse) ──────────────────────────────

    /**
     * Accumulates cursor delta into the orbital rotation angles.
     * Called from MixinMouse instead of applying the delta to player.yaw/pitch.
     *
     * @param dx  raw cursor delta X (screen pixels)
     * @param dy  raw cursor delta Y (screen pixels)
     */
    public void addMouseDelta(double dx, double dy) {
        double sens = sensitivity.getValue();
        orbitYaw   += (float)(dx * sens * 0.15);
        orbitPitch += (float)(dy * sens * 0.15);
        // Clamp pitch to avoid gimbal-lock / pole singularity when the camera
        // is directly above or below the player (see review point 3).
        orbitPitch = Math.max(-89f, Math.min(89f, orbitPitch));
        // Wrap yaw to [-180, 180] to keep values sane (optional but tidy).
        orbitYaw = orbitYaw % 360f;
    }

    // ── Camera override (called from MixinCamera) ─────────────────────────

    /**
     * Computes the orbital camera position and orientation and applies them
     * through the provided functional setters (to avoid calling set* from
     * inside this class, which would require the Camera object).
     *
     * Uses delta-time-based lerp (review point 2) so smoothing looks identical
     * regardless of frame rate.
     *
     * @param tickDelta  fractional tick progress in [0,1]
     * @param frameTimeS actual frame time in seconds (for delta-time lerp)
     * @param setPos     functional reference to Camera.setPos(double, double, double)
     * @param setRot     functional reference to Camera.setRotation(float, float)
     */
    public void applyToCamera(float tickDelta, float frameTimeS,
                               CameraPosSetter setPos, CameraRotSetter setRot) {
        if (!this.nullCheck()) return;

        double rad     = radius.getValue();
        float  yawRad  = (float) Math.toRadians(orbitYaw);
        float  pitchRad = (float) Math.toRadians(orbitPitch);

        // ── Spherical → Cartesian offset ──────────────────────────────────
        double cosPitch = Math.cos(pitchRad);
        double offsetX  = -Math.sin(yawRad) * cosPitch * rad;
        double offsetY  =  Math.sin(pitchRad)            * rad;
        double offsetZ  =  Math.cos(yawRad) * cosPitch  * rad;

        // Eye position of the player (interpolated for smooth render).
        Vec3 eyePos = mc.player.getEyePosition(tickDelta);
        double desiredX = eyePos.x + offsetX;
        double desiredY = eyePos.y + offsetY;
        double desiredZ = eyePos.z + offsetZ;

        // ── Collision (review: same logic as vanilla F5 third-person) ─────
        if (collision.isEnabled()) {
            Vec3 from = eyePos;
            Vec3 to   = new Vec3(desiredX, desiredY, desiredZ);
            BlockHitResult hit = mc.level.clip(new ClipContext(
                from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mc.player
            ));
            if (hit.getType() != HitResult.Type.MISS) {
                Vec3 hp = hit.getLocation();
                // Pull the camera slightly in front of the hit surface (0.1 block
                // margin) to avoid z-fighting with the wall.
                Vec3 dir = to.subtract(from).normalize().scale(0.1);
                desiredX = hp.x - dir.x;
                desiredY = hp.y - dir.y;
                desiredZ = hp.z - dir.z;
            }
        }

        // ── Delta-time smoothing (review point 2) ─────────────────────────
        // Use half-life formula so the smoothing feel is frame-rate independent:
        //   factor = 1 - 0.5^(frameTime / halfLife)
        // With halfLife = 0.08s, the camera covers half the remaining distance
        // every 80 ms, regardless of FPS.
        if (smoothing.isEnabled() && !firstFrame) {
            double halfLife = 0.08;
            double factor   = 1.0 - Math.pow(0.5, frameTimeS / halfLife);
            smoothX += (desiredX - smoothX) * factor;
            smoothY += (desiredY - smoothY) * factor;
            smoothZ += (desiredZ - smoothZ) * factor;
        } else {
            smoothX = desiredX;
            smoothY = desiredY;
            smoothZ = desiredZ;
            firstFrame = false;
        }

        setPos.set(smoothX, smoothY, smoothZ);

        // ── Camera orientation: look back toward the player eye ───────────
        // The orbital offset tells us WHERE the camera is; now we compute
        // what yaw/pitch the camera needs to point AT the player's eye.
        double dx = eyePos.x - smoothX;
        double dy = eyePos.y - smoothY;
        double dz = eyePos.z - smoothZ;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // Pole guard (review point 3): when the camera is almost directly above
        // or below the player, horizontalDist → 0 and atan2 becomes unstable.
        // We clamp to at least 0.001 to get a stable atan2 result.
        float lookYaw;
        if (horizontalDist < 0.001) {
            // Keep the previous yaw, only adjust pitch to straight up/down.
            lookYaw = orbitYaw + 180f;
        } else {
            lookYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        }
        float lookPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDist));

        setRot.set(lookYaw, lookPitch);
    }

    // ── Functional interfaces (avoids passing the Camera object) ──────────

    @FunctionalInterface
    public interface CameraPosSetter {
        void set(double x, double y, double z);
    }

    @FunctionalInterface
    public interface CameraRotSetter {
        void set(float yaw, float pitch);
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public float getOrbitYaw()   { return orbitYaw; }
    public float getOrbitPitch() { return orbitPitch; }

    @Override
    public String getInfoString() {
        return String.format("R:%.1f", radius.getValue());
    }
}
