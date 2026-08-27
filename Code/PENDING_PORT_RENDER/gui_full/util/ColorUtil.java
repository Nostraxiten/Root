/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.gui.util;

import java.awt.Color;

public class ColorUtil {
    public static int BACKGROUND = -870704594;
    public static int BACKGROUND_DARKER = -586215654;
    public static int PANEL_HEADER = -15326914;
    public static int ACCENT_PRIMARY = -8635667;
    public static int ACCENT_SECONDARY = -16337196;
    public static int ACCENT_GRADIENT_START = -8635667;
    public static int ACCENT_GRADIENT_END = -16337196;
    public static int MODULE_ENABLED = -14498466;
    public static int MODULE_DISABLED = -9735552;
    public static int TEXT_PRIMARY = -1;
    public static int TEXT_SECONDARY = -6250320;
    public static int TEXT_ACCENT = -8635667;
    public static int SLIDER_BG = -13816508;
    public static int SLIDER_FILL = -8635667;
    public static int HOVER = 0x40FFFFFF;
    public static int SEPARATOR = -14013890;
    public static int SEARCH_BG = -14803406;
    public static int SCROLLBAR = 0x60FFFFFF;

    public static void updateTheme(int gradStart, int gradEnd, int bg, int textPrimary, int activeModule) {
        ACCENT_GRADIENT_START = gradStart;
        ACCENT_GRADIENT_END = gradEnd;
        ACCENT_PRIMARY = gradStart;
        ACCENT_SECONDARY = gradEnd;
        TEXT_ACCENT = gradStart;
        SLIDER_FILL = gradStart;
        BACKGROUND = bg;
        BACKGROUND_DARKER = ColorUtil.withAlpha(bg, Math.min(255, ColorUtil.alpha(bg) + 20));
        PANEL_HEADER = ColorUtil.lerp(bg, gradStart, 0.1f);
        PANEL_HEADER = ColorUtil.withAlpha(PANEL_HEADER, 255);
        SEARCH_BG = ColorUtil.lerp(bg, -1, 0.05f);
        SEPARATOR = ColorUtil.lerp(bg, -1, 0.15f);
        SLIDER_BG = ColorUtil.lerp(bg, -1, 0.15f);
        TEXT_PRIMARY = textPrimary;
        MODULE_ENABLED = activeModule;
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int alpha(int color) {
        return color >> 24 & 0xFF;
    }

    public static int red(int color) {
        return color >> 16 & 0xFF;
    }

    public static int green(int color) {
        return color >> 8 & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int withAlpha(int color, int alpha) {
        return alpha << 24 | color & 0xFFFFFF;
    }

    public static int lerp(int color1, int color2, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int a = (int)((float)ColorUtil.alpha(color1) + (float)(ColorUtil.alpha(color2) - ColorUtil.alpha(color1)) * t);
        int r = (int)((float)ColorUtil.red(color1) + (float)(ColorUtil.red(color2) - ColorUtil.red(color1)) * t);
        int g = (int)((float)ColorUtil.green(color1) + (float)(ColorUtil.green(color2) - ColorUtil.green(color1)) * t);
        int b = (int)((float)ColorUtil.blue(color1) + (float)(ColorUtil.blue(color2) - ColorUtil.blue(color1)) * t);
        return ColorUtil.rgba(r, g, b, a);
    }

    public static int rainbow(float offset, float saturation, float brightness) {
        float hue = ((float)(System.currentTimeMillis() % 3000L) / 3000.0f + offset) % 1.0f;
        return Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
    }
}

