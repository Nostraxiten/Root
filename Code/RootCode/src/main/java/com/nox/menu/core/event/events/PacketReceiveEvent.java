/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 */
package com.nox.menu.core.event.events;

import com.nox.menu.core.event.Event;
import net.minecraft.network.protocol.Packet;

public class PacketReceiveEvent
extends Event {
    private final Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}

