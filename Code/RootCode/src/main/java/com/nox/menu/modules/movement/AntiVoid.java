/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$PositionAndOnGround
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class AntiVoid
extends Module {
    private final NumberSetting distance = new NumberSetting("Fall Distance", 5.0, 3.0, 20.0, 1.0);
    private Vec3 lastSafePos;

    public AntiVoid() {
        super("AntiVoid", "Te salva de caer al vacio. | Saves you from falling into the void.", Category.MOVEMENT);
        this.addSetting(this.distance);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (AntiVoid.mc.player.onGround()) {
            this.lastSafePos = new net.minecraft.world.phys.Vec3(AntiVoid.mc.player.getX(), AntiVoid.mc.player.getY(), AntiVoid.mc.player.getZ());
        } else if (this.lastSafePos != null && AntiVoid.mc.player.fallDistance >= (Double)this.distance.getValue() && AntiVoid.mc.player.getY() < (double)AntiVoid.mc.level.dimensionType().minY()) {
            AntiVoid.mc.player.connection.send((Packet)new ServerboundMovePlayerPacket.Pos(this.lastSafePos.x, this.lastSafePos.y, this.lastSafePos.z, true, false));
            AntiVoid.mc.player.setPos(this.lastSafePos.x, this.lastSafePos.y, this.lastSafePos.z);
            AntiVoid.mc.player.setDeltaMovement(0.0, 0.0, 0.0);
            AntiVoid.mc.player.fallDistance = 0.0;
        }
    }
}

