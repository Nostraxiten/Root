package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AutoDodge extends Module {
    private final NumberSetting reactionRadius = new NumberSetting("Reaction Radius", 15.0, 5.0, 30.0, 1.0);
    private final NumberSetting sidestepStrength = new NumberSetting("Sidestep Strength", 0.5, 0.1, 2.0, 0.1);

    public AutoDodge() {
        super("AutoDodge", "Esquiva automaticamente proyectiles entrantes | Automatically dodges incoming projectiles", Category.COMBAT);
        this.addSetting(this.reactionRadius);
        this.addSetting(this.sidestepStrength);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        double radius = this.reactionRadius.getValue();
        AABB searchBox = mc.player.getBoundingBox().inflate(radius);

        for (Entity entity : mc.level.getEntities(mc.player, searchBox, e -> true)) {
            if (!(entity instanceof Projectile)) continue;

            Vec3 projPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            Vec3 projVel = entity.getDeltaMovement();

            // Ignore stationary or very slow projectiles
            if (projVel.lengthSqr() < 0.05) continue;

            // Extrapolate projectile path for next 10 ticks
            Vec3 futureProjPos = projPos.add(projVel.scale(10.0));

            // Player bounding box slightly expanded to be safe
            AABB playerBox = mc.player.getBoundingBox().inflate(0.3);

            // Check if the path intersects the player
            if (playerBox.clip(projPos, futureProjPos).isPresent()) {
                // Calculate dodge direction (perpendicular to horizontal velocity)
                Vec3 horizontalVel = new Vec3(projVel.x, 0, projVel.z).normalize();
                if (horizontalVel.lengthSqr() < 0.01) continue;

                // Rotate 90 degrees to get a perpendicular vector
                Vec3 dodgeDir = new Vec3(-horizontalVel.z, 0, horizontalVel.x);

                // Determine which way to dodge (left or right) based on current movement or randomness
                // Here we just pick a direction. To be smarter, we can check which side is closer to center,
                // but a simple push is often enough. We'll use a consistent direction based on projectile's entity ID
                if (entity.getId() % 2 == 0) {
                    dodgeDir = dodgeDir.scale(-1);
                }

                dodgeDir = dodgeDir.scale(this.sidestepStrength.getValue());
                
                // Apply velocity
                mc.player.addDeltaMovement(new Vec3(dodgeDir.x, 0, dodgeDir.z));
                
                // Jump if on ground might help too, but sidestep is requested
                
                break; // Only dodge one projectile per tick to avoid erratic movement
            }
        }
    }
}
