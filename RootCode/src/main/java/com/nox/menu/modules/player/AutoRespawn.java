/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.DeathScreen
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawn
extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Reaparece instantaneamente al morir. | Respawns instantly on death.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (AutoRespawn.mc.player == null) {
            return;
        }
        if (AutoRespawn.mc.player.isDeadOrDying() || AutoRespawn.mc.gui.screen() instanceof DeathScreen) {
            AutoRespawn.mc.player.respawn();
            mc.gui.setScreen(null);
        }
    }
}

