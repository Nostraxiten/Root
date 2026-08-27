package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;

public class TriggerBot extends Module {
    private final NumberSetting cps = new NumberSetting("CPS", 10.0, 1.0, 20.0, 1.0);

    private int tickCounter = 0;
    private int lastTargetId = -1; // -1 = sin objetivo previo

    public TriggerBot() {
        super("TriggerBot", "Ataca automaticamente cuando miras a una entidad. | Attacks automatically when crosshair is on an entity.", Category.COMBAT);
        this.addSetting(this.cps);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }

        Entity target = getCurrentTarget();

        if (target == null) {
            // Sin objetivo: reseteamos el estado para que el proximo target
            // dispare el golpe instantaneo, no un golpe "a medio contador"
            this.lastTargetId = -1;
            this.tickCounter = 0;
            return;
        }

        boolean isNewTarget = target.getId() != this.lastTargetId;

        if (isNewTarget) {
            // Reaccion instantanea: primer golpe en el mismo tick que se detecta
            // el objetivo, sin esperar al ciclo de CPS
            this.lastTargetId = target.getId();
            this.tickCounter = 0;
            attack(target);
            return;
        }

        // Mismo objetivo que el tick anterior: aqui si aplica el ritmo de CPS
        ++this.tickCounter;
        int ticksPerHit = (int) (20.0 / (double) this.cps.getValue());
        if (ticksPerHit <= 0) {
            ticksPerHit = 1;
        }

        if (this.tickCounter >= ticksPerHit) {
            this.tickCounter = 0;
            attack(target);
        }
    }

    private Entity getCurrentTarget() {
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) {
            return null;
        }
        Entity target = ((EntityHitResult) mc.hitResult).getEntity();
        if (!target.isAlive() || target == mc.player) {
            return null;
        }
        return target;
    }

    private void attack(Entity target) {
        mc.gameMode.attack((Player) mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
    }
}