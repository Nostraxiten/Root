/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class AntiKnockback
extends Module {
    private final NumberSetting horizontal = new NumberSetting("Horizontal %", 0.0, 0.0, 100.0, 1.0);
    private final NumberSetting vertical = new NumberSetting("Vertical %", 0.0, 0.0, 100.0, 1.0);

    public AntiKnockback() {
        super("AntiKnockback", "Reduce o cancela el empuje al recibir golpes. | Reduces or cancels knockback from hits.", Category.COMBAT);
        this.addSetting(this.horizontal);
        this.addSetting(this.vertical);
    }

    public double getHorizontalMultiplier() {
        return (Double)this.horizontal.getValue() / 100.0;
    }

    public double getVerticalMultiplier() {
        return (Double)this.vertical.getValue() / 100.0;
    }
}

