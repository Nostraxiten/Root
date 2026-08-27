/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

public class HitboxExpand
extends Module {
    private final NumberSetting expand = new NumberSetting("Expand", 0.5, 0.0, 2.0, 0.1);

    public HitboxExpand() {
        super("HitboxExpand", "Expande las hitboxes enemigas para apuntar mejor. | Expands enemy hitboxes for easier aiming.", Category.COMBAT);
        this.addSetting(this.expand);
    }

    public double getExpandAmount() {
        return (Double)this.expand.getValue();
    }
}

