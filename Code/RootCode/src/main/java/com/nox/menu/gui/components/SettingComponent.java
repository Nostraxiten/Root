package com.nox.menu.gui.components;

import com.nox.menu.core.setting.Setting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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

    public abstract void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY);

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
}
