package com.nox.menu.gui.components;

import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.gui.util.AnimationUtil;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class BooleanComponent
extends SettingComponent {
    private static final int HEIGHT = 14;
    private float animation = 0.0f;

    public BooleanComponent(BooleanSetting setting, int width) {
        super(setting, width);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        BooleanSetting boolSetting = (BooleanSetting)this.setting;
        float target = (Boolean)boolSetting.getValue() != false ? 1.0f : 0.0f;
        this.animation = AnimationUtil.animate(this.animation, target, 0.2f);
        context.text(font, this.setting.getName(), this.x + 2, this.y + 3, ColorUtil.TEXT_SECONDARY, true);
        int boxSize = 8;
        int boxX = this.x + this.width - boxSize - 4;
        int boxY = this.y + (14 - boxSize) / 2;
        RenderUtil.drawRect(context, boxX, boxY, boxSize, boxSize, ColorUtil.BACKGROUND);
        if (this.animation > 0.01f) {
            int fillSize = (int)((float)boxSize * this.animation);
            int offset = (boxSize - fillSize) / 2;
            RenderUtil.drawRect(context, boxX + offset, boxY + offset, fillSize, fillSize, ColorUtil.MODULE_ENABLED);
        }
        RenderUtil.drawOutline(context, boxX, boxY, boxSize, boxSize, ColorUtil.SEPARATOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + 14)) {
            ((BooleanSetting)this.setting).toggle();
            return true;
        }
        return false;
    }
}
