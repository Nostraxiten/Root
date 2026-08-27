/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 */
package com.nox.menu.gui.components;

import com.nox.menu.core.setting.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public abstract class SettingComponent {
    protected final Setting<?> setting;
    protected final int width;
    protected int x;
    protected int y;

    public SettingComponent(Setting<?> setting, int width) {
        this.setting = setting;
        this.width = width;
    }

    public Setting<?> getSetting() {
        return this.setting;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract int getHeight();

    public abstract void render(DrawContext var1, TextRenderer var2, int var3, int var4);

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
}

