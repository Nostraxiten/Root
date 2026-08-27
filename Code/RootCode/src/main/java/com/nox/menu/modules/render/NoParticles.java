/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;

public class NoParticles
extends Module {
    public NoParticles() {
        super("NoParticles", "Desactiva todas las particulas para mejor rendimiento. | Disables all particles for better performance.", Category.OPTIMIZE);
        this.setEnabled(true);
    }
}

