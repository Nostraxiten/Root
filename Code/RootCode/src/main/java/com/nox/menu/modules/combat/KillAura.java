/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.AxeItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$LookAndOnGround
 *  net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$FluidHandling
 *  net.minecraft.world.level.ClipContext$ShapeType
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.modules.combat.HitboxExpand;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;

public class KillAura
extends Module {
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Always", "Always", "OnHit", "None");
    private final BooleanSetting onlyOnClick = new BooleanSetting("Only On Click", false);
    private final BooleanSetting onlyOnLook = new BooleanSetting("Only On Look", false);
    private final BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", true);
    private final BooleanSetting swapBack = new BooleanSetting("Swap Back", false);
    private final ModeSetting weaponCheck = new ModeSetting("Weapon Check", "Any", "Any", "Sword", "Axe", "Sword/Axe");
    private final ModeSetting shieldMode = new ModeSetting("Shield Mode", "Ignore", "Ignore", "Break", "None");
    private final NumberSetting range = new NumberSetting("Range", 6.0, 1.0, 6.0, 0.1);
    private final NumberSetting wallsRange = new NumberSetting("Walls Range", 0.0, 0.0, 6.0, 0.1);
    private final NumberSetting fov = new NumberSetting("FOV", 360.0, 30.0, 360.0, 5.0);
    private final ModeSetting targetMode = new ModeSetting("Target", "All", "Players", "Hostiles", "Mobs", "All");
    private final ModeSetting priority = new ModeSetting("Priority", "Angle", "Distance", "Health", "Angle");
    private final NumberSetting maxTargets = new NumberSetting("Max Targets", 1.0, 1.0, 5.0, 1.0);
    private final BooleanSetting ignoreNamed = new BooleanSetting("Ignore Named", false);
    private final BooleanSetting ignorePassive = new BooleanSetting("Ignore Passive", false);
    private final BooleanSetting ignoreTamed = new BooleanSetting("Ignore Tamed", true);
    private final BooleanSetting pauseOnLag = new BooleanSetting("Pause On Lag", true);
    private final BooleanSetting pauseOnUse = new BooleanSetting("Pause On Use", false);
    private final BooleanSetting tpsSync = new BooleanSetting("TPS Sync", true);
    private final NumberSetting cooldownThreshold = new NumberSetting("Cooldown %", 0.9, 0.5, 1.0, 0.01);
    private final BooleanSetting humanizedTiming = new BooleanSetting("Humanized Timing", true);
    private final NumberSetting jitterAmount = new NumberSetting("Jitter", 0.2, 0.0, 0.2, 0.01);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 1.0, 0.0, 10.0, 1.0);
    private final BooleanSetting smoothRotation = new BooleanSetting("Smooth Rotation", true);
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", 20.0, 5.0, 180.0, 1.0);
    private final BooleanSetting aimNoise = new BooleanSetting("Aim Noise", true);
    private final NumberSetting aimNoiseAmount = new NumberSetting("Aim Noise Amount", 0.0, 0.0, 3.0, 0.1);
    private final BooleanSetting visualCamera = new BooleanSetting("Visual Camera", true);
    private final BooleanSetting autoMove = new BooleanSetting("Auto Move (Hold W)", false);
    private final NumberSetting followSpeed = new NumberSetting("Follow Speed", 0.1, 0.1, 0.5, 0.02);
    private final NumberSetting keepDistance = new NumberSetting("Keep Distance", 1.0, 0.3, 1.0, 0.05);
    private final List<LivingEntity> targets = new ArrayList<LivingEntity>();
    private float serverYaw;
    private float serverPitch;
    private int switchTimer = 0;
    private int previousSlot = -1;
    private boolean swapped = false;
    private long lastTickNanos = 0L;
    private double estimatedTps = 20.0;

    public KillAura() {
        super("KillAura", "Ataca automaticamente a entidades cercanas en rango. | Automatically attacks nearby entities in range.", Category.COMBAT);
        this.addSetting(this.rotationMode);
        this.addSetting(this.onlyOnClick);
        this.addSetting(this.onlyOnLook);
        this.addSetting(this.autoSwitch);
        this.addSetting(this.swapBack);
        this.addSetting(this.weaponCheck);
        this.addSetting(this.shieldMode);
        this.addSetting(this.range);
        this.addSetting(this.wallsRange);
        this.addSetting(this.fov);
        this.addSetting(this.targetMode);
        this.addSetting(this.priority);
        this.addSetting(this.maxTargets);
        this.addSetting(this.ignoreNamed);
        this.addSetting(this.ignorePassive);
        this.addSetting(this.ignoreTamed);
        this.addSetting(this.pauseOnLag);
        this.addSetting(this.pauseOnUse);
        this.addSetting(this.tpsSync);
        this.addSetting(this.cooldownThreshold);
        this.addSetting(this.humanizedTiming);
        this.addSetting(this.jitterAmount);
        this.addSetting(this.switchDelay);
        this.addSetting(this.smoothRotation);
        this.addSetting(this.rotationSpeed);
        this.addSetting(this.aimNoise);
        this.addSetting(this.aimNoiseAmount);
        this.addSetting(this.visualCamera);
        this.addSetting(this.autoMove);
        this.addSetting(this.followSpeed);
        this.addSetting(this.keepDistance);
    }

    @Override
    public void onEnable() {
        if (this.nullCheck()) {
            this.serverYaw = KillAura.mc.player.getYRot();
            this.serverPitch = KillAura.mc.player.getXRot();
        }
        this.targets.clear();
        this.previousSlot = -1;
        this.swapped = false;
        this.lastTickNanos = 0L;
    }

    @Override
    public void onDisable() {
        this.stopAttacking();
        this.targets.clear();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        this.updateTpsEstimate();
        if (this.targets.isEmpty()) {
            this.serverYaw = KillAura.mc.player.getYRot();
            this.serverPitch = KillAura.mc.player.getXRot();
        }
        if (KillAura.mc.player.isDeadOrDying() || KillAura.mc.player.getHealth() <= 0.0f) {
            this.stopAttacking();
            return;
        }
        if (this.pauseOnUse.isEnabled() && (KillAura.mc.player.isUsingItem() || KillAura.mc.gameMode.isDestroying())) {
            this.stopAttacking();
            return;
        }
        if (this.onlyOnClick.isEnabled() && !KillAura.mc.options.keyAttack.isDown()) {
            this.stopAttacking();
            return;
        }
        if (this.pauseOnLag.isEnabled() && this.estimatedTps < 15.0) {
            this.stopAttacking();
            return;
        }
        this.findTargets();
        if (this.targets.isEmpty()) {
            this.stopAttacking();
            return;
        }
        LivingEntity primary = this.targets.get(0);
        this.handleAutoMove(primary);
        if (this.autoSwitch.isEnabled()) {
            this.handleAutoSwitch(primary);
        }
        if (!this.isHoldingAcceptableWeapon()) {
            this.stopAttacking();
            return;
        }
        if ("Always".equals(this.rotationMode.getValue())) {
            this.rotateTo(primary);
        }
        if (this.switchTimer > 0) {
            --this.switchTimer;
            return;
        }
        if (this.delayCheck()) {
            for (LivingEntity target : this.targets) {
                this.attack(target);
            }
        }
    }

    private void findTargets() {
        this.targets.clear();
        if (this.onlyOnLook.isEnabled()) {
            LivingEntity living;
            Entity class_12972 = KillAura.mc.crosshairPickEntity;
            if (class_12972 instanceof LivingEntity && this.isValidTarget(living = (LivingEntity)class_12972)) {
                this.targets.add(living);
            }
            return;
        }
        ArrayList<LivingEntity> candidates = new ArrayList<LivingEntity>();
        double expand = this.getHitboxExpand();
        for (Entity entity : KillAura.mc.level.entitiesForRendering()) {
            double wallsMax;
            LivingEntity living;
            if (!(entity instanceof LivingEntity) || !this.isValidTarget(living = (LivingEntity)entity)) continue;
            AABB box = living.getBoundingBox().inflate(expand);
            double dist = KillAura.mc.player.getEyePosition().distanceTo(box.getCenter());
            boolean hasLos = KillAura.mc.player.hasLineOfSight((Entity) living);
            if (dist > (Double)this.range.getValue() ? (wallsMax = ((Double)this.wallsRange.getValue()).doubleValue()) <= 0.0 || dist > wallsMax || hasLos : !hasLos && (Double)this.wallsRange.getValue() <= 0.0 && this.isBlockedByBlock(box.getCenter())) continue;
            candidates.add(living);
        }
        if (candidates.isEmpty()) {
            return;
        }
        switch ((String)this.priority.getValue()) {
            case "Health": {
                candidates.sort(Comparator.comparingDouble(LivingEntity::getHealth));
                break;
            }
            case "Angle": {
                candidates.sort(Comparator.comparingDouble(this::angleDifferenceTo));
                break;
            }
            default: {
                candidates.sort(Comparator.comparingDouble(e -> KillAura.mc.player.distanceTo((Entity)e)));
            }
        }
        int max = ((Double)this.maxTargets.getValue()).intValue();
        for (int i = 0; i < Math.min(max, candidates.size()); ++i) {
            this.targets.add((LivingEntity)candidates.get(i));
        }
    }

    private boolean isValidTarget(LivingEntity living) {
        Animal animal;
        TamableAnimal tameable;
        if (living == KillAura.mc.player || living.isRemoved() || !living.isAlive() || living.getHealth() <= 0.0f) {
            return false;
        }
        String mode = (String)this.targetMode.getValue();
        boolean isPlayer = living instanceof Player;
        boolean isHostile = living instanceof Monster;
        switch (mode) {
            case "Players": {
                if (isPlayer) break;
                return false;
            }
            case "Hostiles": {
                if (isHostile) break;
                return false;
            }
            case "Mobs": {
                if (!isPlayer) break;
                return false;
            }
        }
        if (this.ignoreNamed.isEnabled() && living.hasCustomName()) {
            return false;
        }
        if (this.ignoreTamed.isEnabled() && living instanceof TamableAnimal && (tameable = (TamableAnimal)living).isTame()) {
            return false;
        }
        if (this.ignorePassive.isEnabled() && living instanceof Animal && (animal = (Animal)living).getLastHurtByMob() != KillAura.mc.player) {
            return false;
        }
        return this.isEntityInFov(living);
    }

    private boolean isBlockedByBlock(Vec3 point) {
        BlockHitResult result = KillAura.mc.level.clip(new ClipContext(KillAura.mc.player.getEyePosition(), point, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)KillAura.mc.player));
        return result.getType() == HitResult.Type.BLOCK;
    }

    private double getHitboxExpand() {
        HitboxExpand hitboxModule = ModuleManager.getInstance().getModule(HitboxExpand.class);
        if (hitboxModule != null && hitboxModule.isEnabled()) {
            return hitboxModule.getExpandAmount();
        }
        return 0.0;
    }

    private boolean isEntityInFov(LivingEntity entity) {
        return this.angleDifferenceTo(entity) <= (Double)this.fov.getValue() / 2.0;
    }

    private double angleDifferenceTo(LivingEntity entity) {
        float[] rotations = this.getRotationsTo(this.getAimPoint(entity));
        return Math.abs(Mth.wrapDegrees((float)(KillAura.mc.player.getYRot() - rotations[0])));
    }

    private Vec3 getAimPoint(LivingEntity entity) {
        return entity.getBoundingBox().getCenter();
    }

    private void rotateTo(LivingEntity target) {
        float finalPitch;
        float finalYaw;
        float[] targetRotations = this.getRotationsTo(this.getAimPoint(target));
        if (this.aimNoise.isEnabled()) {
            double noise = (Double)this.aimNoiseAmount.getValue();
            targetRotations[0] = targetRotations[0] + (float)(Math.random() * noise * 2.0 - noise);
            targetRotations[1] = targetRotations[1] + (float)(Math.random() * noise * 2.0 - noise);
        }
        if (this.smoothRotation.isEnabled()) {
            float maxStep = ((Double)this.rotationSpeed.getValue()).floatValue();
            float yawDiff = Mth.wrapDegrees((float)(targetRotations[0] - this.serverYaw));
            float pitchDiff = Mth.wrapDegrees((float)(targetRotations[1] - this.serverPitch));
            finalYaw = this.serverYaw + Mth.clamp((float)yawDiff, (float)(-maxStep), (float)maxStep);
            finalPitch = Mth.clamp((float)(this.serverPitch + Mth.clamp((float)pitchDiff, (float)(-maxStep), (float)maxStep)), (float)-90.0f, (float)90.0f);
        } else {
            finalYaw = targetRotations[0];
            finalPitch = Mth.clamp((float)targetRotations[1], (float)-90.0f, (float)90.0f);
        }
        this.serverYaw = finalYaw;
        this.serverPitch = finalPitch;
        if (this.visualCamera.isEnabled()) {
            KillAura.mc.player.setYRot(this.serverYaw);
            KillAura.mc.player.setXRot(this.serverPitch);
        }
        KillAura.mc.player.connection.send((Packet)new ServerboundMovePlayerPacket.Rot(this.serverYaw, this.serverPitch, KillAura.mc.player.onGround(), false));
    }

    private float[] getRotationsTo(Vec3 point) {
        Vec3 eyes = KillAura.mc.player.getEyePosition();
        double diffX = point.x - eyes.x;
        double diffY = point.y - eyes.y;
        double diffZ = point.z - eyes.z;
        double horizontalDist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, horizontalDist)));
        return new float[]{yaw, pitch};
    }

    private void handleAutoMove(LivingEntity target) {
        double stopAt;
        if (!this.autoMove.isEnabled() || KillAura.mc.player == null || target == null) {
            return;
        }
        if (!KillAura.mc.options.keyUp.isDown()) {
            return;
        }
        double dist = KillAura.mc.player.distanceTo((Entity)target);
        if (dist <= (stopAt = (Double)this.range.getValue() * (Double)this.keepDistance.getValue())) {
            return;
        }
        Vec3 playerPos = new Vec3(KillAura.mc.player.getX(), KillAura.mc.player.getY(), KillAura.mc.player.getZ());
        Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
        double dx = targetPos.x - playerPos.x;
        double dz = targetPos.z - playerPos.z;
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        if (horizDist < 0.1) {
            return;
        }
        double speed = (Double)this.followSpeed.getValue();
        double moveX = dx / horizDist * speed;
        double moveZ = dz / horizDist * speed;
        KillAura.mc.player.setDeltaMovement(moveX, KillAura.mc.player.getDeltaMovement().y, moveZ);
    }

    private void handleAutoSwitch(LivingEntity target) {
        Player player;
        boolean shouldBreakShield = "Break".equals(this.shieldMode.getValue()) && target instanceof Player && (player = (Player)target).isBlocking();
        int bestSlot = this.findBestWeaponSlot(shouldBreakShield);
        if (bestSlot == -1) {
            return;
        }
        int currentSlot = KillAura.mc.player.getInventory().getSelectedSlot();
        if (bestSlot == currentSlot) {
            return;
        }
        if (!this.swapped) {
            this.previousSlot = currentSlot;
            this.swapped = true;
        }
        KillAura.mc.player.getInventory().setSelectedSlot(bestSlot);
        KillAura.mc.player.connection.send((Packet)new ServerboundSetCarriedItemPacket(bestSlot));
        this.switchTimer = ((Double)this.switchDelay.getValue()).intValue();
    }

    private int findBestWeaponSlot(boolean needsAxe) {
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack stack = KillAura.mc.player.getInventory().getItem(slot);
            if (!(needsAxe ? stack.getItem() instanceof AxeItem : this.matchesWeaponCheck(stack))) continue;
            return slot;
        }
        return -1;
    }

    private boolean isHoldingAcceptableWeapon() {
        return this.matchesWeaponCheck(KillAura.mc.player.getMainHandItem());
    }

    private boolean matchesWeaponCheck(ItemStack stack) {
        String mode;
        return switch (mode = (String)this.weaponCheck.getValue()) {
            case "Sword" -> stack.is(h -> h.is(ItemTags.SWORDS));
            case "Axe" -> stack.getItem() instanceof AxeItem;
            case "Sword/Axe" -> {
                if (stack.is(h -> h.is(ItemTags.SWORDS)) || stack.getItem() instanceof AxeItem) {
                    yield true;
                }
                yield false;
            }
            default -> true;
        };
    }

    private void stopAttacking() {
        if (this.swapBack.isEnabled() && this.swapped) {
            KillAura.mc.player.getInventory().setSelectedSlot(this.previousSlot);
            KillAura.mc.player.connection.send((Packet)new ServerboundSetCarriedItemPacket(this.previousSlot));
            this.swapped = false;
        }
        this.targets.clear();
    }

    private void updateTpsEstimate() {
        long now = System.nanoTime();
        if (this.lastTickNanos != 0L) {
            double deltaMs = (double)(now - this.lastTickNanos) / 1000000.0;
            double instantTps = Mth.clamp((double)(1000.0 / Math.max(deltaMs, 1.0)), (double)1.0, (double)20.0);
            this.estimatedTps = this.estimatedTps * 0.9 + instantTps * 0.1;
        }
        this.lastTickNanos = now;
    }

    private boolean delayCheck() {
        float threshold = ((Double)this.cooldownThreshold.getValue()).floatValue();
        if (this.tpsSync.isEnabled() && this.estimatedTps > 0.0) {
            threshold *= (float)(20.0 / this.estimatedTps);
            threshold = Mth.clamp((float)threshold, (float)0.3f, (float)1.5f);
        }
        if (this.humanizedTiming.isEnabled()) {
            threshold += (float)(Math.random() * (Double)this.jitterAmount.getValue());
        }
        threshold = Mth.clamp((float)threshold, (float)0.0f, (float)0.99f);
        return KillAura.mc.player.getAttackStrengthScale(0.0f) >= threshold;
    }

    private void attack(LivingEntity target) {
        if ("OnHit".equals(this.rotationMode.getValue())) {
            this.rotateTo(target);
        }
        KillAura.mc.gameMode.attack((Player)KillAura.mc.player, (Entity)target);
        KillAura.mc.player.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public String getInfoString() {
        String string;
        if (this.targets.isEmpty()) {
            return null;
        }
        Entity target = (Entity)this.targets.get(0);
        if (target instanceof Player) {
            Player player = (Player)target;
            string = player.getName().getString();
        } else {
            string = target.getType().toString();
        }
        return string;
    }
}

