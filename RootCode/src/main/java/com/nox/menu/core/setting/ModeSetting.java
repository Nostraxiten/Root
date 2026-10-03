/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core.setting;

import com.nox.menu.core.setting.Setting;
import java.util.Arrays;
import java.util.List;

public class ModeSetting
extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String defaultValue, String ... modes) {
        super(name, defaultValue);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
        return this.modes;
    }

    public int getCurrentIndex() {
        return this.modes.indexOf(this.getValue());
    }

    public void cycle() {
        int nextIndex = (this.getCurrentIndex() + 1) % this.modes.size();
        this.setValue(this.modes.get(nextIndex));
    }

    public boolean is(String mode) {
        return ((String)this.getValue()).equalsIgnoreCase(mode);
    }
}

