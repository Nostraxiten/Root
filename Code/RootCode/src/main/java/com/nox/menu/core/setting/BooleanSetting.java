/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core.setting;

import com.nox.menu.core.setting.Setting;

public class BooleanSetting
extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public boolean isEnabled() {
        return (Boolean)this.getValue();
    }

    public void toggle() {
        this.setValue((Boolean)this.getValue() == false);
    }
}

