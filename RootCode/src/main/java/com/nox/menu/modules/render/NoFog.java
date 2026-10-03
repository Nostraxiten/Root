/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;

public class NoFog
extends Module {
    public NoFog() {
        super("NoFog", "Elimina toda la niebla (agua, lava, distancia). | Removes all fog (water, lava, distance).", Category.OPTIMIZE);
        this.setEnabled(true);
    }
}

