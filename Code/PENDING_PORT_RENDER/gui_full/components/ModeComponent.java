/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 */
package com.nox.menu.gui.components;

import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.gui.components.SettingComponent;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ModeComponent
extends SettingComponent {
    private static final int HEIGHT = 14;

    public ModeComponent(ModeSetting setting, int width) {
        super(setting, width);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        ModeSetting modeSetting = (ModeSetting)this.setting;
        context.drawTextWithShadow(textRenderer, this.setting.getName() + ":", this.x + 2, this.y + 3, ColorUtil.TEXT_SECONDARY);
        String mode = (String)modeSetting.getValue();
        context.drawTextWithShadow(textRenderer, mode, this.x + this.width - textRenderer.getWidth(mode) - 4, this.y + 3, ColorUtil.TEXT_PRIMARY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + 14)) {
            ((ModeSetting)this.setting).cycle();
            return true;
        }
        return false;
    }
}

