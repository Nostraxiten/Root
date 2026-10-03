/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.item.Items
 */
package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;

public class AutoTotem
extends Module {
    private final NumberSetting delayTicks = new NumberSetting("Delay", 2.0, 0.0, 5.0, 1.0);
    private int cooldown = 0;
    private int totemSlot = -1;
    private int screenSlot = -1;

    public AutoTotem() {
        super("AutoTotem", "Equipa un Totem en la mano secundaria al estar bajo de salud. | Auto-equips Totem of Undying in offhand when low health.", Category.COMBAT);
        this.addSetting(this.delayTicks);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (AutoTotem.mc.player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return;
        }
        if (!AutoTotem.mc.player.containerMenu.getCarried().isEmpty()) {
            return;
        }
        this.totemSlot = this.findTotemSlot();
        if (this.totemSlot == -1) {
            return;
        }
        this.screenSlot = this.totemSlot < 9 ? this.totemSlot + 36 : this.totemSlot;
        int syncId = AutoTotem.mc.player.inventoryMenu.containerId;
        AutoTotem.mc.gameMode.handleContainerInput(syncId, this.screenSlot, 40, ContainerInput.SWAP, (Player)AutoTotem.mc.player);
        this.cooldown = ((Number)this.delayTicks.getValue()).intValue();
    }

    private int findTotemSlot() {
        int i;
        for (i = 0; i < 9; ++i) {
            if (AutoTotem.mc.player.getInventory().getItem(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
            return i;
        }
        for (i = 9; i < 36; ++i) {
            if (AutoTotem.mc.player.getInventory().getItem(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
            return i;
        }
        return -1;
    }

    public boolean hasTotemEquipped() {
        if (!this.nullCheck()) {
            return false;
        }
        return AutoTotem.mc.player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING || AutoTotem.mc.player.getMainHandItem().getItem() == Items.TOTEM_OF_UNDYING;
    }
}

