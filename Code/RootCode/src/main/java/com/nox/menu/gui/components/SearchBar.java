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

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        int textColor;
        String displayText;
        ++this.cursorTick;
        int bgColor = this.focused ? ColorUtil.withAlpha(ColorUtil.SEARCH_BG, 255) : ColorUtil.SEARCH_BG;
        RenderUtil.drawRect(context, this.x, this.y, this.width, this.height, bgColor);
        int borderColor = this.focused ? ColorUtil.ACCENT_PRIMARY : ColorUtil.SEPARATOR;
        RenderUtil.drawOutline(context, this.x, this.y, this.width, this.height, borderColor);
        if (this.text.isEmpty() && !this.focused) {
            displayText = "🔍 Search modules...";
            textColor = ColorUtil.TEXT_SECONDARY;
        } else {
            displayText = this.text;
            textColor = ColorUtil.TEXT_PRIMARY;
        }
        String rendered = displayText + (this.focused && this.cursorTick / 15 % 2 == 0 ? "|" : "");
        context.text(font, rendered, this.x + 5, this.y + (this.height - 8) / 2, textColor, true);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean wasInside;
        this.focused = wasInside = mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + this.height);
        return wasInside;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.focused) {
            return false;
        }
        if (keyCode == 259 && !this.text.isEmpty()) {
            this.text = this.text.substring(0, this.text.length() - 1);
            return true;
        }
        if (keyCode == 256) {
            this.focused = false;
            this.text = "";
            return true;
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!this.focused) {
            return false;
        }
        if (Character.isLetterOrDigit(chr) || chr == ' ' || chr == '_') {
            this.text = this.text + chr;
            return true;
        }
        return false;
    }
}
