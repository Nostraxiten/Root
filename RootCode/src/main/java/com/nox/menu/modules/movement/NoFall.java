package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.IServerboundMovePlayerPacket;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.event.EventBus;
import com.nox.menu.core.event.events.PacketSendEvent;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    private final Consumer<PacketSendEvent> packetListener = this::onPacketSend;

    public NoFall() {
        super("NoFall", "Elimina el dano por caida | Prevents fall damage", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        EventBus.getInstance().subscribe(PacketSendEvent.class, this.packetListener);
    }

    @Override
    public void onDisable() {
        EventBus.getInstance().unsubscribe(PacketSendEvent.class, this.packetListener);
    }

    private void onPacketSend(PacketSendEvent event) {
        if (!this.nullCheck()) return;

        Packet<?> packet = event.getPacket();
        if (!(packet instanceof ServerboundMovePlayerPacket)) {
            return;
        }

        Fly fly = ModuleManager.getInstance().getModule(Fly.class);
        boolean flyActive = fly != null && fly.isEnabled();

        // Spoofea onGround=true si hay fallDistance acumulada O si Fly está activo
        // (Fly puede resetear fallDistance en otros escenarios; no depender solo de ese campo)
        if (flyActive || mc.player.fallDistance > 2.0f) {
            ((IServerboundMovePlayerPacket) packet).noxMenu$setOnGround(true);
        }
    }
}