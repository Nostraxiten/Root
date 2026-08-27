package com.nox.menu.gui.components;

import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ColorComponent
extends SettingComponent {
    private final ColorSetting colorSetting;
    private boolean draggingHue = false;
    private boolean draggingSat = false;
    private boolean draggingBri = false;
    private boolean draggingAlpha = false;
    private float hue = 0.0f;
    private float sat = 1.0f;
    private float bri = 1.0f;
    private float alpha = 1.0f;

    public ColorComponent(ColorSetting setting, int width) {
        super(setting, width);
        this.colorSetting = setting;
        this.updateHSBFromSetting();
    }

    private void updateHSBFromSetting() {
        int color = (Integer)this.colorSetting.getValue();
        int r = ColorUtil.red(color);
        int g = ColorUtil.green(color);
        int b = ColorUtil.blue(color);
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        this.hue = hsb[0];
        this.sat = hsb[1];
        this.bri = hsb[2];
        this.alpha = ColorUtil.alpha(color) / 255f;
    }

    private void updateSettingFromHSB() {
        int rgb = Color.HSBtoRGB(this.hue, this.sat, this.bri);
        int alphaInt = Math.round(this.alpha * 255f);
        this.colorSetting.setValue(ColorUtil.rgba(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb), alphaInt));
    }

    @Override
    public int getHeight() {
        return 52;
    }

    @Override
    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        context.text(font, this.colorSetting.getName(), this.x, this.y + 2, ColorUtil.TEXT_PRIMARY, true);
        int previewW = 10;
        int previewH = 10;
        int previewX = this.x + this.width - previewW;
        RenderUtil.drawRect(context, previewX, this.y + 1, previewW, previewH, (Integer)this.colorSetting.getValue());
        RenderUtil.drawOutline(context, previewX, this.y + 1, previewW, previewH, ColorUtil.TEXT_SECONDARY);
        int sliderH = 8;
        int hueY = this.y + 12;
        int segW = this.width / 6;
        int[] rainbowColors = new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536};
        for (int i = 0; i < 6; ++i) {
            int currentX = this.x + i * segW;
            int currentW = i == 5 ? this.width - currentX : segW;
            RenderUtil.drawHorizontalGradient(context, currentX, hueY, currentW, sliderH, rainbowColors[i], rainbowColors[i + 1]);
        }
        int hueHandleX = this.x + (int)(this.hue * (float)this.width);
        RenderUtil.drawRect(context, hueHandleX - 1, hueY - 1, 2, sliderH + 2, -1);

        int satY = hueY + 10;
        int hueOnlyColor = Color.HSBtoRGB(this.hue, 1.0f, 1.0f) | 0xFF000000;
        int whiteColor = -1;
        RenderUtil.drawHorizontalGradient(context, this.x, satY, this.width, sliderH, whiteColor, hueOnlyColor);
        int satHandleX = this.x + (int)(this.sat * (float)this.width);
        RenderUtil.drawRect(context, satHandleX - 1, satY - 1, 2, sliderH + 2, -16777216);
        RenderUtil.drawRect(context, satHandleX, satY, 1, sliderH, -1);

        int briY = satY + 10;
        int maxColor = Color.HSBtoRGB(this.hue, this.sat, 1.0f) | 0xFF000000;
        RenderUtil.drawHorizontalGradient(context, this.x, briY, this.width, sliderH, -16777216, maxColor);
        int briHandleX = this.x + (int)(this.bri * (float)this.width);
        RenderUtil.drawRect(context, briHandleX - 1, briY - 1, 2, sliderH + 2, -1);

        int alphaY = briY + 10;
        int opaqueColor = (Color.HSBtoRGB(this.hue, this.sat, this.bri) & 0xFFFFFF) | 0xFF000000;
        int transparentColor = opaqueColor & 0xFFFFFF;
        RenderUtil.drawHorizontalGradient(context, this.x, alphaY, this.width, sliderH, transparentColor, opaqueColor);
        int alphaHandleX = this.x + (int)(this.alpha * (float)this.width);
        RenderUtil.drawRect(context, alphaHandleX - 1, alphaY - 1, 2, sliderH + 2, -1);

        if (this.draggingHue) {
            this.hue = (float)(mouseX - this.x) / (float)this.width;
            this.hue = Math.max(0.0f, Math.min(1.0f, this.hue));
            this.updateSettingFromHSB();
        }
        if (this.draggingSat) {
            this.sat = (float)(mouseX - this.x) / (float)this.width;
            this.sat = Math.max(0.0f, Math.min(1.0f, this.sat));
            this.updateSettingFromHSB();
        }
        if (this.draggingBri) {
            this.bri = (float)(mouseX - this.x) / (float)this.width;
            this.bri = Math.max(0.0f, Math.min(1.0f, this.bri));
            this.updateSettingFromHSB();
        }
        if (this.draggingAlpha) {
            this.alpha = (float)(mouseX - this.x) / (float)this.width;
            this.alpha = Math.max(0.0f, Math.min(1.0f, this.alpha));
            this.updateSettingFromHSB();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int sliderH = 8;
            int hueY = this.y + 12;
            int satY = hueY + 10;
            int briY = satY + 10;
            int alphaY = briY + 10;
            if (mouseY >= (double)hueY && mouseY <= (double)(hueY + sliderH) && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width)) {
                this.draggingHue = true;
                return true;
            }
            if (mouseY >= (double)satY && mouseY <= (double)(satY + sliderH) && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width)) {
                this.draggingSat = true;
                return true;
            }
            if (mouseY >= (double)briY && mouseY <= (double)(briY + sliderH) && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width)) {
                this.draggingBri = true;
                return true;
            }
            if (mouseY >= (double)alphaY && mouseY <= (double)(alphaY + sliderH) && mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width)) {
                this.draggingAlpha = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingHue = false;
        this.draggingSat = false;
        this.draggingBri = false;
        this.draggingAlpha = false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.draggingHue || this.draggingSat || this.draggingBri || this.draggingAlpha;
    }
}
