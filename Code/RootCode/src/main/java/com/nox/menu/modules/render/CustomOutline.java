package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.core.setting.NumberSetting;
import java.awt.Color;

/**
 * CustomOutline - Changes the color and thickness of the block selection outline.
 */
public class CustomOutline extends Module {

    public final ColorSetting color = new ColorSetting("Color", 0x66000000); // Default vanilla color (black with alpha)
    public final NumberSetting width = new NumberSetting("Width", 2.0, 5.0, 1.0, 0.5);

    public static boolean isRenderingOutline = false;

    public CustomOutline() {
        super("CustomOutline", "Modifica el color y grosor del borde de los bloques. | Modifies the block outline color and thickness.", Category.RENDER);
        this.addSetting(color);
        this.addSetting(width);
    }

    public int getColor() {
        return color.getValue();
    }

    public float getWidth() {
        return width.getFloatValue();
    }
}
