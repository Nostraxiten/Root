package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class Sit extends Module {
    private ArmorStandEntity sitEntity;
    private final int ENTITY_ID = -42069;

    private static final int TOGGLE_COOLDOWN_TICKS = 4;
    private long lastDisableTick = -1000;

    public Sit() {
        super("Sit", "Te sienta en el suelo | Sits your player on the ground", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) {
            this.setEnabled(false);
            return;
        }

        long worldTime = mc.world.getTime();
        if (worldTime - lastDisableTick < TOGGLE_COOLDOWN_TICKS) {
            this.setEnabled(false);
            return;
        }

        mc.player.setVelocity(0, 0, 0);
        mc.player.fallDistance = 0f;

        sitEntity = new ArmorStandEntity(EntityType.ARMOR_STAND, mc.world);
        sitEntity.setId(ENTITY_ID);
        sitEntity.setPosition(mc.player.getX(), mc.player.getY() - 1.9, mc.player.getZ());

        float yaw = mc.player.getYaw();
        syncEntityYaw(yaw);

        sitEntity.setNoGravity(true);
        sitEntity.setInvisible(true);

        mc.world.addEntity(sitEntity);
        mc.player.startRiding(sitEntity, true, true);
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) return;

        if (sitEntity != null) {
            mc.player.stopRiding();

            mc.player.setVelocity(0, 0, 0);
            mc.player.fallDistance = 0f;

            mc.world.removeEntity(ENTITY_ID, net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            sitEntity = null;
        }

        lastDisableTick = mc.world.getTime();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        if (sitEntity != null && !mc.player.hasVehicle()) {
            this.setEnabled(false);
            return;
        }
        if (mc.options.sneakKey.isPressed()) {
            this.setEnabled(false);
            return;
        }
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck() || sitEntity == null) return;

        float yaw = mc.player.getYaw();
        syncEntityYaw(yaw);
    }

    private void syncEntityYaw(float yaw) {
        sitEntity.setYaw(yaw);
        sitEntity.setHeadYaw(yaw);
        sitEntity.setBodyYaw(yaw);
        sitEntity.headYaw = yaw;
        sitEntity.bodyYaw = yaw;
    }
}