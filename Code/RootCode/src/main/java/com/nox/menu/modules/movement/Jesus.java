package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Jesus — walk on water/lava.
 *
 * Approach for 1.21.5:
 *  The core trick is velocity manipulation only (no position teleport).
 *
 *  1. Find the liquid surface Y at the player's XZ.
 *  2. If the player is INSIDE the liquid (py < surfaceY): push upward.
 *  3. If the player is AT or just ABOVE the surface (within a small margin)
 *     AND falling: stop the fall and flag onGround so MC stops applying gravity.
 *  4. After a jump the player goes up and comes back down — case 3 catches it
 *     when vel.y turns negative again, regardless of how high they jumped.
 *  5. Sneaking sinks normally.
 */
public class Jesus extends Module {

    // How far above the surface we still consider the player "on the fluid"
    // (covers the tiny gap between surface and feet after a jump lands)
    private static final double SURFACE_MARGIN = 0.15;

    public Jesus() {
        super("Jesus", "Camina sobre agua y lava como si fueran solidas. | Walk on water and lava as if solid.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;
        if (mc.player.isShiftKeyDown()) return;

        double py = mc.player.getY();
        double px = mc.player.getX();
        double pz = mc.player.getZ();

        double surfaceY = findLiquidSurfaceY(px, py, pz);
        if (surfaceY < 0) return;

        Vec3 vel = mc.player.getDeltaMovement();
        double diff = surfaceY - py;   // positive → player below surface; negative → player above

        if (diff > 0) {
            // Inside the liquid: push up toward surface
            double nudge = Math.min(diff + 0.05, 0.15);
            mc.player.setDeltaMovement(vel.x, nudge, vel.z);
        } else if (diff >= -SURFACE_MARGIN && vel.y <= 0) {
            // Just at/above surface and not jumping: cancel gravity and flag ground
            mc.player.setDeltaMovement(vel.x, 0.0, vel.z);
            mc.player.setOnGround(true);
        }

        // Lava fire suppression
        if (mc.player.isInLava()) {
            mc.player.setRemainingFireTicks(0);
        }
    }

    /**
     * Finds the Y coordinate of the top of the highest liquid block touching the player.
     * Checks up to 3 blocks downward from foot position.
     */
    private double findLiquidSurfaceY(double x, double y, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        // Start one block above feet to catch cases where player's feet are
        // exactly at block boundary and fluid height < 1
        int startBy = (int) Math.floor(y) + 1;
        for (int dy = 0; dy <= 3; dy++) {
            int by = startBy - dy;
            BlockPos pos = new BlockPos(bx, by, bz);
            FluidState fluid = mc.level.getFluidState(pos);
            if (!fluid.isEmpty()) {
                double top = by + fluid.getHeight(mc.level, pos);
                // Only use this surface if it's within reach of the player's feet
                if (top >= y - 0.5) {
                    return top;
                }
            }
        }
        return -1;
    }
}