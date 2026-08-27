/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.core.component.DataComponents
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

public class AutoEat
extends Module {
    private final NumberSetting threshold = new NumberSetting("Hunger Threshold", 15.0, 1.0, 19.0, 1.0);
    private boolean eating = false;
    private int oldSlot = -1;

    public AutoEat() {
        super("AutoEat", "Come automaticamente cuando el hambre baja. | Auto-eats food when hunger drops.", Category.PLAYER);
        this.addSetting(this.threshold);
    }

    @Override
    public void onTick() {
        int foodSlot;
        if (!this.nullCheck()) {
            return;
        }
        if (this.eating) {
            if (!AutoEat.mc.player.isUsingItem()) {
                this.eating = false;
                AutoEat.mc.options.keyUse.setDown(false);
                if (this.oldSlot != -1) {
                    AutoEat.mc.player.getInventory().setSelectedSlot(this.oldSlot);
                    this.oldSlot = -1;
                }
            }
            return;
        }
        if ((double)AutoEat.mc.player.getFoodData().getFoodLevel() <= (Double)this.threshold.getValue() && (foodSlot = this.findFood()) != -1) {
            this.oldSlot = AutoEat.mc.player.getInventory().getSelectedSlot();
            AutoEat.mc.player.getInventory().setSelectedSlot(foodSlot);
            AutoEat.mc.options.keyUse.setDown(true);
            AutoEat.mc.gameMode.useItem((Player)AutoEat.mc.player, InteractionHand.MAIN_HAND);
            this.eating = true;
        }
    }

    private int findFood() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AutoEat.mc.player.getInventory().getItem(i);
            if (stack.isEmpty() || !stack.getItem().components().has(DataComponents.FOOD)) continue;
            return i;
        }
        return -1;
    }
}

