package com.nox.menu.modules.movement;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.item.DyeColor;

public class Sit extends Module {
    private final BooleanSetting invisible = new BooleanSetting("Invisible", false);
    private final ModeSetting color = new ModeSetting("Color", "Red", "Red", "Blue", "Green", "Purple", "Black", "White");
    private final NumberSetting heightOffset = new NumberSetting("Height Offset", 1.88, 0.5, 3.0, 0.02);
    private Entity sitEntity;
    private final int ENTITY_ID = -42069;

    private static final int TOGGLE_COOLDOWN_TICKS = 4;
    private long lastDisableTick = -1000;

    public Sit() {
        super("Sit", "Te sienta en un cojin (Cushion) o asiento invisible en el suelo | Sits your player on a cushion or invisible seat", Category.MOVEMENT);
        this.addSetting(this.invisible);
        this.addSetting(this.color);
        this.addSetting(this.heightOffset);
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

        boolean isInvis = this.invisible.getValue();
        if (isInvis) {
            ArmorStand stand = new ArmorStand(EntityTypes.ARMOR_STAND, mc.level);
            stand.setPos(mc.player.getX(), mc.player.getY() - this.heightOffset.getValue(), mc.player.getZ());
            stand.setId(ENTITY_ID);
            stand.setInvisible(true);
            sitEntity = stand;
        } else {
            Cushion cushion = new Cushion(EntityTypes.CUSHION, mc.level);
            cushion.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
            cushion.setId(ENTITY_ID);
            try {
                cushion.setColor(DyeColor.valueOf(this.color.getValue().toUpperCase()));
            } catch (Exception e) {
                cushion.setColor(DyeColor.RED);
            }
            sitEntity = cushion;
        }

        float yaw = mc.player.getYRot();
        syncEntityYaw(yaw);

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
    }
}
