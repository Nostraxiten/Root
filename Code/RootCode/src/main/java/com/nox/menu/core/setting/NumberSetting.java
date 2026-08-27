/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core.setting;

import com.nox.menu.core.setting.Setting;

public class NumberSetting
extends Setting<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getIncrement() {
        return this.increment;
    }

    public int getIntValue() {
        return (int)Math.round((Double)this.getValue());
    }

    public float getFloatValue() {
        return ((Double)this.getValue()).floatValue();
    }

    @Override
    public void setValue(Double value) {
        double clamped = Math.max(this.min, Math.min(this.max, value));
        double snapped = (double)Math.round(clamped / this.increment) * this.increment;
        super.setValue(snapped);
    }

    public double getNormalized() {
        return ((Double)this.getValue() - this.min) / (this.max - this.min);
    }

    public void setFromNormalized(double normalized) {
        this.setValue(this.min + normalized * (this.max - this.min));
    }
}

