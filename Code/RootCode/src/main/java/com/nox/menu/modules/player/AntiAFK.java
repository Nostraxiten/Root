/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Input
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class AntiAFK
extends Module {
    private final NumberSetting minInterval = new NumberSetting("Min Interval", 40.0, 10.0, 200.0, 1.0);
    private final NumberSetting maxInterval = new NumberSetting("Max Interval", 100.0, 20.0, 400.0, 1.0);
    private final NumberSetting lookNoise = new NumberSetting("Look Noise", 1.0, 0.0, 10.0, 0.1);
    private final NumberSetting strafeTicksSetting = new NumberSetting("Strafe Ticks", 10.0, 2.0, 40.0, 1.0);
    private int ticksUntilNextAction = 0;
    private int strafeTicksRemaining = 0;
    private float currentStrafeDirection = 1.0f;

    public AntiAFK() {
        super("AntiAFK", "Realiza microacciones para evitar ser expulsado por inactividad. | Performs micro-actions to avoid AFK kick.", Category.PLAYER);
        this.addSetting(this.minInterval);
        this.addSetting(this.maxInterval);
        this.addSetting(this.lookNoise);
        this.addSetting(this.strafeTicksSetting);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.resetTimers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (AntiAFK.mc.player != null && AntiAFK.mc.player.input != null && AntiAFK.mc.player.input.keyPresses != null) {
            Input cur = AntiAFK.mc.player.input.keyPresses;
            AntiAFK.mc.player.input.keyPresses = new Input(cur.forward(), cur.backward(), false, false, cur.jump(), cur.shift(), cur.sprint());
        }
        this.strafeTicksRemaining = 0;
    }

    private void resetTimers() {
        int max;
        if (AntiAFK.mc.player == null) {
            return;
        }
        int min = this.minInterval.getIntValue();
        if (min >= (max = this.maxInterval.getIntValue())) {
            max = min + 1;
        }
        this.ticksUntilNextAction = Mth.nextInt(AntiAFK.mc.player.getRandom(), min, max);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        RandomSource random = AntiAFK.mc.player.getRandom();
        float noise = this.lookNoise.getFloatValue();
        if (noise > 0.0f) {
            float yawNoise = (random.nextFloat() * 2.0f - 1.0f) * noise;
            float pitchNoise = (random.nextFloat() * 2.0f - 1.0f) * noise;
            AntiAFK.mc.player.setYRot(AntiAFK.mc.player.getYRot() + yawNoise);
            AntiAFK.mc.player.setXRot(Mth.clamp((float)(AntiAFK.mc.player.getXRot() + pitchNoise), (float)-90.0f, (float)90.0f));
        }
        if (this.strafeTicksRemaining > 0) {
            boolean left = this.currentStrafeDirection > 0.0f;
            boolean right = this.currentStrafeDirection < 0.0f;
            Input cur = AntiAFK.mc.player.input.keyPresses;
            AntiAFK.mc.player.input.keyPresses = new Input(cur.forward(), cur.backward(), left, right, cur.jump(), cur.shift(), cur.sprint());
            --this.strafeTicksRemaining;
        } else {
            Input cur = AntiAFK.mc.player.input.keyPresses;
            AntiAFK.mc.player.input.keyPresses = new Input(cur.forward(), cur.backward(), false, false, cur.jump(), cur.shift(), cur.sprint());
        }
        --this.ticksUntilNextAction;
        if (this.ticksUntilNextAction <= 0) {
            int chance = random.nextInt(100);
            if (chance < 40) {
                this.strafeTicksRemaining = this.strafeTicksSetting.getIntValue() + random.nextInt(5);
                this.currentStrafeDirection = random.nextBoolean() ? 1.0f : -1.0f;
            } else if (chance < 75) {
                AntiAFK.mc.player.swing(InteractionHand.MAIN_HAND);
            } else if (AntiAFK.mc.player.onGround()) {
                // No hay un jump() publico directo; se simula con el impulso vertical vanilla.
                net.minecraft.world.phys.Vec3 v = AntiAFK.mc.player.getDeltaMovement();
                AntiAFK.mc.player.setDeltaMovement(v.x, 0.42, v.z);
            }
            this.resetTimers();
        }
    }
}

