/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.event.EventBus;
import com.nox.menu.core.event.events.PacketReceiveEvent;
import com.nox.menu.modules.combat.AntiKnockback;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public abstract class MixinClientPacketListener {
    @Inject(method={"handleSetEntityMotion"}, at={@At(value="HEAD")}, cancellable=true)
    private void onVelocityUpdate(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        if (packet.id() != client.player.getId()) {
            return;
        }
        PacketReceiveEvent event = EventBus.getInstance().post(new PacketReceiveEvent((Packet<?>)packet));
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        AntiKnockback antiKB = manager.getModule(AntiKnockback.class);
        if (antiKB != null && antiKB.isEnabled()) {
            double hMult = antiKB.getHorizontalMultiplier();
            double vMult = antiKB.getVerticalMultiplier();
            ci.cancel();
            net.minecraft.world.phys.Vec3 vel = packet.movement();
            client.player.setDeltaMovement(vel.x * hMult, vel.y * vMult, vel.z * hMult);
        }
    }
}

