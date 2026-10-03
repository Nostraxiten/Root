/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core.setting;

import java.util.function.BooleanSupplier;

public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private BooleanSupplier visibility;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.visibility = () -> true;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public Setting<T> setVisibility(BooleanSupplier visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return this.visibility.getAsBoolean();
    }
}

