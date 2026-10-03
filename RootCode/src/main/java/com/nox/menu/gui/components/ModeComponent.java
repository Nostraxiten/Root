package com.nox.menu.gui.components;

import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        ModeSetting modeSetting = (ModeSetting)this.setting;
        context.text(font, this.setting.getName() + ":", this.x + 2, this.y + 3, ColorUtil.TEXT_SECONDARY, true);
        String mode = (String)modeSetting.getValue();
        context.text(font, mode, this.x + this.width - font.width(mode) - 4, this.y + 3, ColorUtil.TEXT_PRIMARY, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((button == 1 || button == 0) && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + 14)) {
            ((ModeSetting)this.setting).cycle();
            return true;
        }
        return false;
    }
}
