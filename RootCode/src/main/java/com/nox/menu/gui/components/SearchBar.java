package com.nox.menu.gui.components;

import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SearchBar {
    private int x;
    private int y;
    private int width;
    private int height;
    private String text = "";
    private boolean focused = false;
    private int cursorTick = 0;
    private long lastCharTime = 0;

    public SearchBar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return this.width;
    }

    public String getText() {
        return this.text;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        ++this.cursorTick;
        int bgColor = this.focused ? ColorUtil.withAlpha(ColorUtil.SEARCH_BG, 255) : ColorUtil.SEARCH_BG;
        RenderUtil.drawRect(context, this.x, this.y, this.width, this.height, bgColor);
        int borderColor = this.focused ? ColorUtil.ACCENT_PRIMARY : ColorUtil.SEPARATOR;
        RenderUtil.drawOutline(context, this.x, this.y, this.width, this.height, borderColor);
        
        String displayText;
        int textColor;
        if (this.text.isEmpty() && !this.focused) {
            displayText = "🔍 Search modules...";
            textColor = ColorUtil.TEXT_SECONDARY;
        } else {
            displayText = this.text;
            textColor = ColorUtil.TEXT_PRIMARY;
        }
        String rendered = displayText + (this.focused && (this.cursorTick / 15) % 2 == 0 ? "|" : "");
        context.text(font, rendered, this.x + 5, this.y + (this.height - 8) / 2, textColor, true);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean wasInside = mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) 
                         && mouseY >= (double)this.y && mouseY <= (double)(this.y + this.height);
        this.focused = wasInside;
        return wasInside;
    }

    public static boolean isEscapeKey(int key, int keycode) {
        return key == 41 || key == 256 || key == 27 || keycode == 27 || keycode == 256 || keycode == 41;
    }

    public static boolean isBackspaceKey(int key, int keycode) {
        return key == 42 || key == 259 || key == 8 || keycode == 8 || keycode == 259 || keycode == 42;
    }

    public static boolean isDeleteKey(int key, int keycode) {
        return key == 76 || key == 261 || key == 127 || keycode == 127 || keycode == 261 || keycode == 76;
    }

    public static boolean isEnterKey(int key, int keycode) {
        return key == 40 || key == 257 || key == 13 || keycode == 13 || keycode == 257 || keycode == 40;
    }

    public boolean keyPressed(int key, int keycode, int modifiers) {
        if (!this.focused) {
            return false;
        }
        if (isBackspaceKey(key, keycode)) {
            if (!this.text.isEmpty()) {
                this.text = this.text.substring(0, this.text.length() - 1);
            }
            return true;
        }
        if (isEscapeKey(key, keycode) || isEnterKey(key, keycode)) {
            this.focused = false;
            return true;
        }
        if (isDeleteKey(key, keycode)) {
            this.text = "";
            return true;
        }

        // Direct key mapping fallback (in case charTyped is not dispatched by windowing layer)
        char c = keyToChar(key, keycode, modifiers);
        if (c != 0) {
            this.text = this.text + c;
            this.lastCharTime = System.currentTimeMillis();
            return true;
        }

        return true; // Consume any other key while search bar is focused
    }

    private char keyToChar(int key, int keycode, int modifiers) {
        boolean shift = (modifiers & 1) != 0;
        int code = keycode > 0 ? keycode : key;
        if (code >= 97 && code <= 122) { // a-z
            return shift ? (char) (code - 32) : (char) code;
        }
        if (code >= 65 && code <= 90) { // A-Z
            return shift ? (char) code : (char) (code + 32);
        }
        if (code >= 48 && code <= 57) { // 0-9
            if (!shift) return (char) code;
            char[] shiftNums = {')', '!', '@', '#', '$', '%', '^', '&', '*', '('};
            return shiftNums[code - 48];
        }
        if (code == 32) return ' ';
        if (code == 45) return shift ? '_' : '-';
        if (code == 46) return '.';
        if (code == 47) return '/';
        return 0;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!this.focused) {
            return false;
        }
        // If keyPressed already handled this character recently, ignore to prevent duplicate characters
        if (System.currentTimeMillis() - this.lastCharTime < 60) {
            return true;
        }
        if (chr >= 32 && chr != 127) {
            this.text = this.text + chr;
            return true;
        }
        return false;
    }
}

