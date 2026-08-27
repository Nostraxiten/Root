package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

public class ChestStealer extends Module {

    // 0 = instantaneo (todos los slots el mismo tick, apertura -> inventario lleno al instante)
    // valores mas altos = 1 click cada N ticks (mas "humano" a ojos de logs de timing)
    private final NumberSetting delayTicks = new NumberSetting("Delay (ticks)", 0, 0, 10, 1);

    private int tickCounter = 0;

    public ChestStealer() {
        super("ChestStealer", "Transfiere automaticamente el contenido de cofres a tu inventario. | Auto-transfers chest contents to your inventory.", Category.PLAYER);
        this.addSetting(this.delayTicks);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }

        Screen currentScreen = ChestStealer.mc.gui.screen();
        if (!(currentScreen instanceof ContainerScreen)) {
            return;
        }

        ContainerScreen screen = (ContainerScreen) currentScreen;
        ChestMenu handler = (ChestMenu) screen.getMenu();
        int inventorySize = handler.getContainer().getContainerSize();

        int delay = this.delayTicks.getIntValue();

        if (delay <= 0) {
            // Modo instantaneo: todos los slots ocupados se vacian en el mismo tick.
            // Nota: esto genera N packets QUICK_MOVE en el mismo tick, lo cual es
            // trivialmente detectable por cualquier logger de timing de clicks —
            // si te importa el sigilo, usa delay >= 1.
            for (int i = 0; i < inventorySize; ++i) {
                if (handler.getSlot(i).hasItem()) {
                    ChestStealer.mc.gameMode.handleContainerInput(
                        handler.containerId, i, 0, ContainerInput.QUICK_MOVE, ChestStealer.mc.player
                    );
                }
            }
        } else {
            // Modo con delay: 1 slot cada N ticks, igual que la version original
            ++this.tickCounter;
            if (this.tickCounter < delay) {
                return;
            }
            this.tickCounter = 0;

            for (int i = 0; i < inventorySize; ++i) {
                if (!handler.getSlot(i).hasItem()) continue;
                ChestStealer.mc.gameMode.handleContainerInput(
                    handler.containerId, i, 0, ContainerInput.QUICK_MOVE, ChestStealer.mc.player
                );
                return; // solo 1 click por ciclo de delay
            }
        }

        // Cierre automatico si el cofre quedo vacio (igual que la version original)
        boolean empty = true;
        for (int i = 0; i < inventorySize; ++i) {
            if (handler.getSlot(i).hasItem()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            ChestStealer.mc.gui.setScreen(null);
        }
    }
}