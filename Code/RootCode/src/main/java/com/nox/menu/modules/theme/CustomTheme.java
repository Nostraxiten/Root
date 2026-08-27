package com.nox.menu.modules.theme;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.gui.util.ColorUtil;

public class CustomTheme
extends Module {
    private final ColorSetting gradientStart = new ColorSetting("Grad Start", -8635667);
    private final ColorSetting gradientEnd = new ColorSetting("Grad End", -16337196);
    private final ColorSetting background = new ColorSetting("Background", -870704594);
    private final ColorSetting textPrimary = new ColorSetting("Text", -1);
    private final ColorSetting activeModuleColor = new ColorSetting("Active", -14498466);

    public CustomTheme() {
        super("CustomTheme", "Personaliza los colores del cliente | Customize the client color theme", Category.THEME);
        this.addSetting(this.gradientStart);
        this.addSetting(this.gradientEnd);
        this.addSetting(this.background);
        this.addSetting(this.textPrimary);
        this.addSetting(this.activeModuleColor);
    }

    @Override
    public void onTick() {
        if (!this.isEnabled()) {
            return;
        }
        ColorUtil.updateTheme(this.gradientStart.getValue(), this.gradientEnd.getValue(), this.background.getValue(), this.textPrimary.getValue(), this.activeModuleColor.getValue());
    }
}
