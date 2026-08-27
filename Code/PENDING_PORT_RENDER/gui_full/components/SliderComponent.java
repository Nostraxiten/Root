/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 */
package com.nox.menu.gui.components;

import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.gui.components.SettingComponent;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class SliderComponent
extends SettingComponent {
    private static final int HEIGHT = 14;
    private boolean dragging = false;

    public SliderComponent(NumberSetting setting, int width) {
        super(setting, width);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        NumberSetting numSetting = (NumberSetting)this.setting;
        if (this.dragging) {
            double normalized = (double)(mouseX - (this.x + 2)) / (double)(this.width - 4);
            numSetting.setFromNormalized(Math.max(0.0, Math.min(1.0, normalized)));
        }
        context.drawTextWithShadow(textRenderer, this.setting.getName(), this.x + 2, this.y + 3, ColorUtil.TEXT_SECONDARY);
        String valueStr = numSetting.getIncrement() == (double)((int)numSetting.getIncrement()) ? String.valueOf(numSetting.getIntValue()) : String.format("%.1f", Float.valueOf(numSetting.getFloatValue()));
        context.drawTextWithShadow(textRenderer, valueStr, this.x + this.width - textRenderer.getWidth(valueStr) - 4, this.y + 3, ColorUtil.TEXT_PRIMARY);
        int trackY = this.y + 14 - 2;
        RenderUtil.drawRect(context, this.x + 2, trackY, this.width - 4, 2, ColorUtil.SLIDER_BG);
        double normalized = numSetting.getNormalized();
        int fillWidth = (int)((double)(this.width - 4) * normalized);
        RenderUtil.drawRect(context, this.x + 2, trackY, fillWidth, 2, ColorUtil.SLIDER_FILL);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + 14)) {
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.dragging;
    }
}

