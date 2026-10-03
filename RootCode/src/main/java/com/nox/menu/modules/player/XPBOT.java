package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.core.setting.BooleanSetting;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;

public class XPBOT extends Module {
    public final NumberSetting targetLevel = new NumberSetting("Target Level", 30.0, 1.0, 100.0, 1.0);
    public final BooleanSetting visualOnly = new BooleanSetting("Visual Only", true);

    private int realLevel = 0;
    private float realProgress = 0.0f;
    private int realTotal = 0;

    public XPBOT() {
        super("XPBOT", "Simula o grindea niveles de XP automaticamente | Simulates or grinds XP levels automatically", Category.PLAYER);
        this.addSetting(this.targetLevel);
        this.addSetting(this.visualOnly);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) return;
        this.realLevel = mc.player.experienceLevel;
        this.realProgress = mc.player.experienceProgress;
        this.realTotal = mc.player.totalExperience;
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) return;
        if ((Boolean) this.visualOnly.getValue()) {
            mc.player.experienceLevel = this.realLevel;
            mc.player.experienceProgress = this.realProgress;
            mc.player.totalExperience = this.realTotal;
        }
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        if ((Boolean) this.visualOnly.getValue()) {
            mc.player.experienceLevel = this.targetLevel.getIntValue();
        } else {
            if (mc.player.experienceLevel < this.targetLevel.getIntValue()) {
                int xpSlot = -1;
                for (int i = 0; i < 9; i++) {
                    if (mc.player.getInventory().getItem(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                        xpSlot = i;
                        break;
                    }
                }

                if (xpSlot != -1) {
                    mc.player.getInventory().setSelectedSlot(xpSlot);
                    mc.player.setXRot(90f); // Mira hacia abajo para lanzar a los pies
                    mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                }
            } else {
                this.setEnabled(false); // Nivel alcanzado
            }
        }
    }
}
