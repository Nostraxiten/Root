package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class AutoClicker extends Module {
    public final NumberSetting cps = new NumberSetting("CPS", 10.0, 1.0, 40.0, 1.0);
    public final BooleanSetting moreEficiencia = new BooleanSetting("moreEficiencia", false);
    public final BooleanSetting requireHold = new BooleanSetting("requireHold", false);
    private int tickCounter = 0;

    public AutoClicker() {
        super("AutoClicker", "Clica automaticamente al ritmo configurado | Auto-clicks at the configured rate", Category.PLAYER);
        this.addSetting(this.cps);
        this.addSetting(this.moreEficiencia);
        this.addSetting(this.requireHold);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        boolean holdRequired = (Boolean) this.requireHold.getValue();
        boolean shouldClick = holdRequired ? mc.options.keyAttack.isDown() : true;

        if (shouldClick) {
            ++this.tickCounter;
            int ticksPerHit = (int) Math.round(20.0 / (Double) this.cps.getValue());
            if (ticksPerHit <= 0) ticksPerHit = 1;

            if (this.tickCounter >= ticksPerHit) {
                this.tickCounter = 0;
                performClick();
            }
        } else {
            this.tickCounter = 0;
        }
    }

    private void performClick() {
        if (mc.player == null || mc.gameMode == null) return;

        HitResult target = mc.hitResult;
        boolean assistMining = (Boolean) this.moreEficiencia.getValue();

        if (target == null) {
            mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            return;
        }

        switch (target.getType()) {
            case ENTITY -> {
                Entity entity = ((EntityHitResult) target).getEntity();
                mc.gameMode.attack(mc.player, entity);
                mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            }
            case BLOCK -> {
                if (assistMining) {
                    BlockHitResult blockHit = (BlockHitResult) target;
                    mc.gameMode.startDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection());
                }
                mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
            }
            default -> mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
        }
    }
}