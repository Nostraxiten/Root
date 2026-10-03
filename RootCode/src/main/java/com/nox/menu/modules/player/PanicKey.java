/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import net.minecraft.network.chat.Component;

public class PanicKey
extends Module {
    private boolean armed = false;
    private long armedAtMillis = 0L;
    private static final long CONFIRM_WINDOW_MS = 3000L;

    public PanicKey() {
        super("PanicKey", "Desactiva todos los modulos activos a la vez. | Disables all active modules at once.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
        if (PanicKey.mc.player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.armed && now - this.armedAtMillis <= 3000L) {
            this.armed = false;
            ModuleManager manager = ModuleManager.getInstance();
            if (manager != null) {
                manager.disableAll();
            }
            PanicKey.mc.player.sendSystemMessage(Component.literal("\u00a7c[PanicKey] Todos los m\u00f3dulos desactivados."));
        } else {
            this.armed = true;
            this.armedAtMillis = now;
            PanicKey.mc.player.sendSystemMessage(Component.literal("\u00a7e\u00bfEst\u00e1s seguro? Pulsa de nuevo en 3s para confirmar el p\u00e1nico."));
        }
    }

    @Override
    public void setKeyBind(int key) {
    }
}

