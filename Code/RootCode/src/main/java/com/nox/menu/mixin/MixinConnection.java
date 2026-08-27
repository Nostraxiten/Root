/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.Packet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.nox.menu.mixin;

import com.nox.menu.core.event.EventBus;
import com.nox.menu.core.event.events.PacketSendEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Connection.class})
public abstract class MixinConnection {
    @Unique
    private boolean noxMenu$sendingModified = false;

    @Inject(method={"send(Lnet/minecraft/network/protocol/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (this.noxMenu$sendingModified) {
            return;
        }
        PacketSendEvent event = EventBus.getInstance().post(new PacketSendEvent(packet));
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        if (event.getPacket() != packet) {
            ci.cancel();
            this.noxMenu$sendingModified = true;
            ((Connection)(Object)this).send(event.getPacket());
            this.noxMenu$sendingModified = false;
        }
    }
}

