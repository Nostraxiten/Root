package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Corre automaticamente al moverte hacia adelante. | Always sprints while moving forward.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        boolean movingForward = mc.player.input.keyPresses.forward();
        boolean sneaking = mc.player.isShiftKeyDown();
        boolean hasHunger = mc.player.getFoodData().getFoodLevel() > 6; // Vanilla exige >6 para poder sprintar

        boolean shouldSprint = movingForward && !sneaking && hasHunger;

        // Deteccion de flanco: solo tocamos setSprinting cuando el estado
        // realmente necesita cambiar, nunca lo repetimos si ya coincide.
        // Esto es lo que elimina el "spam" de paquetes y el conflicto
        // visual con el sprint nativo de Vanilla.
        if (shouldSprint && !mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        } else if (!shouldSprint && mc.player.isSprinting()) {
            mc.player.setSprinting(false);
        }
    }
}