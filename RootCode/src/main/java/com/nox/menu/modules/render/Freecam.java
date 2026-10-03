/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.lwjgl.glfw.GLFW
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.phys.Vec3;

public class Freecam
extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.5, 5.0, 0.1);
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", "Desmonta la camara para explorar libremente. | Detaches camera to explore freely.", Category.RENDER);
        this.addSetting(this.speed);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) {
            return;
        }
        this.x = Freecam.mc.player.getX();
        this.y = Freecam.mc.player.getY() + (double)Freecam.mc.player.getEyeHeight();
        this.z = Freecam.mc.player.getZ();
        this.yaw = Freecam.mc.player.getYRot();
        this.pitch = Freecam.mc.player.getXRot();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        Freecam.mc.player.setDeltaMovement(0.0, 0.0, 0.0);
        this.yaw = Freecam.mc.player.getYRot();
        this.pitch = Freecam.mc.player.getXRot();
        double yawRad = Math.toRadians(this.yaw);
        Vec3 forward = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vec3 right = new Vec3(Math.cos(yawRad), 0, -Math.sin(yawRad));
        double s = (Double)this.speed.getValue();
        if (InputConstants.isKeyDown(InputConstants.KEY_W)) {
            this.x += forward.x * s;
            this.z += forward.z * s;
        }
        if (InputConstants.isKeyDown(InputConstants.KEY_S)) {
            this.x -= forward.x * s;
            this.z -= forward.z * s;
        }
        if (InputConstants.isKeyDown(InputConstants.KEY_D)) {
            this.x += right.x * s;
            this.z += right.z * s;
        }
        if (InputConstants.isKeyDown(InputConstants.KEY_A)) {
            this.x -= right.x * s;
            this.z -= right.z * s;
        }
        if (InputConstants.isKeyDown(InputConstants.KEY_SPACE)) {
            this.y += s;
        }
        if (InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)) {
            this.y -= s;
        }
    }

    public double getCamX() {
        return this.x;
    }

    public double getCamY() {
        return this.y;
    }

    public double getCamZ() {
        return this.z;
    }

    public float getCamYaw() {
        return this.yaw;
    }

    public float getCamPitch() {
        return this.pitch;
    }
}

