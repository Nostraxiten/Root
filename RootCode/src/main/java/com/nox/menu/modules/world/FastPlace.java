package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class FastPlace extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 0.0, 0.0, 3.0, 1.0);

    public FastPlace() {
        super("FastPlace", "Elimina el retardo al colocar bloques y usar proyectiles | Removes block placement delay", Category.WORLD);
        this.addSetting(this.delay);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;
        if (mc.rightClickDelay > this.delay.getIntValue()) {
            mc.rightClickDelay = this.delay.getIntValue();
        }
    }
}

