package com.nox.menu.modules.movement;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class Sit extends Module {
    private ArmorStand sitEntity;
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

        long worldTime = mc.level.getGameTime();
        if (worldTime - lastDisableTick < TOGGLE_COOLDOWN_TICKS) {
            this.setEnabled(false);
            return;
        }

        mc.player.setDeltaMovement(0, 0, 0);
        mc.player.resetFallDistance();

        sitEntity = new ArmorStand(mc.level, mc.player.getX(), mc.player.getY() - 1.9, mc.player.getZ());
        sitEntity.setId(ENTITY_ID);

        float yaw = mc.player.getYRot();
        syncEntityYaw(yaw);

        sitEntity.setNoGravity(true);
        sitEntity.setInvisible(true);

        mc.level.addEntity(sitEntity);
        mc.player.startRiding(sitEntity, true, true);
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) return;

        if (sitEntity != null) {
            mc.player.stopRiding();

            mc.player.setDeltaMovement(0, 0, 0);
            mc.player.resetFallDistance();

            mc.level.removeEntity(ENTITY_ID, Entity.RemovalReason.DISCARDED);
            sitEntity = null;
        }

        lastDisableTick = mc.level.getGameTime();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        if (sitEntity != null && !mc.player.isPassenger()) {
            this.setEnabled(false);
            return;
        }
        if (mc.options.keyShift.isDown()) {
            this.setEnabled(false);
            return;
        }
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck() || sitEntity == null) return;

        float yaw = mc.player.getYRot();
        syncEntityYaw(yaw);
    }

    private void syncEntityYaw(float yaw) {
        sitEntity.setYRot(yaw);
        sitEntity.setYHeadRot(yaw);
        sitEntity.setYBodyRot(yaw);
        sitEntity.yHeadRot = yaw;
        sitEntity.yBodyRot = yaw;
    }
}
