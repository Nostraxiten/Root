package com.nox.menu.gui.components;

import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.core.setting.ModeSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.core.setting.Setting;
import com.nox.menu.gui.util.AnimationUtil;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;

public class ModuleButton {
    private final Module module;
    private int width;
    private int x;
    private int y;

    public void setWidth(int w) { this.width = w; }
    public int  getWidth()      { return this.width; }

    private static final int BASE_HEIGHT = 14;
    private boolean expanded = false;
    private float expandAnimation = 0.0f;
    private final List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
    private boolean listeningForBind = false;
    private boolean hovered = false;
    private SettingComponent draggingComponent = null;
    private static final int BOX_W = 115;
    private static final int BOX_PAD = 5;
    private static final int BOX_GAP = 10;
    private static final int OK_BTN_W = 32;
    private static final int POPUP_TOTAL_W = 244;
    private boolean showPopup = false;
    public static ModuleButton activePopup = null;
    private int lastPopupX = 0;
    private int lastPopupY = 0;
    private int lastPopupW = 0;
    private int lastPopupH = 0;
    private int lastOkX = 0;
    private int lastOkY = 0;
    private int lastOkW = 0;
    private int lastOkH = 0;

    public ModuleButton(Module module, int width) {
        this.module = module;
        this.width = width;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                BooleanSetting bs = (BooleanSetting)setting;
                this.settingComponents.add(new BooleanComponent(bs, width - 8));
                continue;
            }
            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting)setting;
                this.settingComponents.add(new SliderComponent(ns, width - 8));
                continue;
            }
            if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting)setting;
                this.settingComponents.add(new ModeComponent(ms, width - 8));
                continue;
            }
            if (!(setting instanceof ColorSetting)) continue;
            ColorSetting cs = (ColorSetting)setting;
            this.settingComponents.add(new ColorComponent(cs, width - 8));
        }
    }

    public Module getModule() {
        return this.module;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        int settingsHeight = 0;
        if (this.expanded || this.expandAnimation > 0.01f) {
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                settingsHeight += comp.getHeight() + 1;
            }
        }
        return 14 + (int)((float)settingsHeight * this.expandAnimation);
    }

    private int getHelpBtnX() {
        int rightEdge = this.x + this.width - 2;
        if (!this.settingComponents.isEmpty()) {
            rightEdge = this.x + this.width - 12;
        }
        return rightEdge - 9;
    }

    private boolean isOverHelpBtn(double mx, double my) {
        int hx = this.getHelpBtnX();
        return mx >= (double)hx && mx <= (double)(hx + 8) && my >= (double)(this.y + 1) && my <= (double)(this.y + 14 - 1);
    }

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        int bgColor;
        this.hovered = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + 14;
        float target = this.expanded ? 1.0f : 0.0f;
        this.expandAnimation = AnimationUtil.animate(this.expandAnimation, target, 0.2f);
        int n = bgColor = this.hovered ? ColorUtil.withAlpha(ColorUtil.HOVER, 48) : 0;
        if (this.module.isEnabled()) {
            bgColor = ColorUtil.withAlpha(ColorUtil.ACCENT_PRIMARY, this.hovered ? 80 : 48);
        }
        RenderUtil.drawRect(context, this.x, this.y, this.width, 14, bgColor);
        int dotColor = this.module.isEnabled() ? ColorUtil.MODULE_ENABLED : ColorUtil.MODULE_DISABLED;
        RenderUtil.drawRect(context, this.x + 2, this.y + 4, 4, 6, dotColor);
        int nameColor = this.module.isEnabled() ? ColorUtil.TEXT_PRIMARY : ColorUtil.TEXT_SECONDARY;
        context.text(font, this.module.getName(), this.x + 9, this.y + 3, nameColor, true);
        int helpBtnX = this.getHelpBtnX();
        boolean overHelp = this.isOverHelpBtn(mouseX, mouseY);
        int helpColor = overHelp ? -8892 : -7829368;
        context.text(font, "?", helpBtnX, this.y + 3, helpColor, true);
        if (!this.settingComponents.isEmpty()) {
            int gearX = this.x + this.width - 10;
            int gearColor = this.expanded ? ColorUtil.ACCENT_PRIMARY : ColorUtil.TEXT_SECONDARY;
            context.text(font, "⚙", gearX, this.y + 3, gearColor, true);
        }
        // Draw keybind/listening indicator anchored LEFT of the ? button
        if (this.listeningForBind) {
            String listenStr = "[...]";
            int listenX = helpBtnX - 3 - font.width(listenStr);
            context.text(font, listenStr, listenX, this.y + 3, ColorUtil.ACCENT_SECONDARY, true);
        } else if (this.module.getKeyBind() > 0) {
            String keyName = GLFW.glfwGetKeyName((int)this.module.getKeyBind(), (int)0);
            if (keyName == null) keyName = "?";
            String keyStr = "[" + keyName.toUpperCase() + "]";
            int keyX = helpBtnX - 3 - font.width(keyStr);
            context.text(font, keyStr, keyX, this.y + 3, ColorUtil.TEXT_SECONDARY, true);
        }
        if (this.expandAnimation > 0.01f) {
            int settingY = this.y + 14;
            int totalSettingsHeight = 0;
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                totalSettingsHeight += comp.getHeight() + 1;
            }
            RenderUtil.drawRect(context, this.x + 2, settingY, this.width - 4, (int)((float)totalSettingsHeight * this.expandAnimation), ColorUtil.BACKGROUND_DARKER);
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                comp.setPosition(this.x + 4, settingY);
                comp.render(context, font, mouseX, mouseY);
                settingY += comp.getHeight() + 1;
            }
        }
    }

    public void renderPopupIfOpen(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        if (this.showPopup && activePopup == this) {
            this.renderPopup(context, font, mouseX, mouseY);
        } else if (this.showPopup) {
            this.showPopup = false;
        }
    }

    private void renderPopup(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        int okX;
        String originalDesc = this.module.getDescription();
        String enDesc = this.translateToEnglish(this.module.getName(), originalDesc);
        String esDesc = this.translateToSpanish(this.module.getName(), originalDesc);
        int maxLineW = 105;
        List<String> esLines = this.wrapText(font, esDesc, maxLineW);
        List<String> enLines = this.wrapText(font, enDesc, maxLineW);
        int lineH = 10;
        int labelH = lineH + 2;
        int textRows = Math.max(esLines.size(), enLines.size());
        int boxBodyH = labelH + textRows * lineH + 10;
        int okH = lineH + 6;
        int popupH = boxBodyH + okH + 6;
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();
        int popupX = (screenW - 244) / 2;
        int popupY = screenH - popupH - 12;
        this.lastPopupX = popupX;
        this.lastPopupY = popupY;
        this.lastPopupW = 244;
        this.lastPopupH = popupH;
        int esX = popupX;
        int esY = popupY;
        RenderUtil.drawRect(context, esX + 2, esY + 2, 115, boxBodyH, -2013265920);
        RenderUtil.drawRect(context, esX, esY, 115, boxBodyH, -15592918);
        RenderUtil.drawOutline(context, esX, esY, 115, boxBodyH, -8952116);
        RenderUtil.drawRect(context, esX, esY, 115, labelH, -14540220);
        context.text(font, "§5ES", esX + 5, esY + 2, -3359745, true);
        int ey = esY + labelH + 5;
        for (String line : esLines) {
            context.text(font, line, esX + 5, ey, -3355393, true);
            ey += lineH;
        }
        int enX = popupX + 115 + 10;
        int enY = popupY;
        RenderUtil.drawRect(context, enX + 2, enY + 2, 115, boxBodyH, -2013265920);
        RenderUtil.drawRect(context, enX, enY, 115, boxBodyH, -15786209);
        RenderUtil.drawOutline(context, enX, enY, 115, boxBodyH, -12277078);
        RenderUtil.drawRect(context, enX, enY, 115, labelH, -15061458);
        context.text(font, "§3EN", enX + 5, enY + 2, -7803171, true);
        int eyE = enY + labelH + 5;
        for (String line : enLines) {
            context.text(font, line, enX + 5, eyE, -3342354, true);
            eyE += lineH;
        }
        int okY = popupY + boxBodyH + 4;
        this.lastOkX = okX = popupX + 115 + -11;
        this.lastOkY = okY;
        this.lastOkW = 32;
        this.lastOkH = okH;
        boolean overOk = mouseX >= okX && mouseX <= okX + 32 && mouseY >= okY && mouseY <= okY + okH;
        int okBg = overOk ? -8952116 : -14013878;
        RenderUtil.drawRect(context, okX, okY, 32, okH, okBg);
        RenderUtil.drawOutline(context, okX, okY, 32, okH, -8952116);
        String okStr = "OK";
        int okTxtX = okX + (32 - font.width(okStr)) / 2;
        context.text(font, okStr, okTxtX, okY + 2, overOk ? -1 : -5592372, true);
    }

    private List<String> wrapText(Font font, String text, int maxWidth) {
        ArrayList<String> result = new ArrayList<String>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String test;
            String string = test = current.isEmpty() ? word : String.valueOf(current) + " " + word;
            if (font.width(test) <= maxWidth) {
                current = new StringBuilder(test);
                continue;
            }
            if (!current.isEmpty()) {
                result.add(current.toString());
            }
            current = new StringBuilder(word);
        }
        if (!current.isEmpty()) {
            result.add(current.toString());
        }
        if (result.isEmpty()) {
            result.add("");
        }
        return result;
    }

    private String translateToSpanish(String moduleName, String desc) {
        if (desc == null) return "";
        int idx = desc.indexOf('|');
        if (idx >= 0) return desc.substring(0, idx).trim();
        return desc;
    }

    private String translateToEnglish(String moduleName, String desc) {
        if (desc == null) return "";
        int idx = desc.indexOf('|');
        if (idx >= 0 && idx + 1 < desc.length()) return desc.substring(idx + 1).trim();
        return desc;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.showPopup && button == 0) {
            if (mouseX >= (double)this.lastOkX && mouseX <= (double)(this.lastOkX + this.lastOkW) && mouseY >= (double)this.lastOkY && mouseY <= (double)(this.lastOkY + this.lastOkH)) {
                this.showPopup = false;
                return true;
            }
            if (mouseX >= (double)this.lastPopupX && mouseX <= (double)(this.lastPopupX + this.lastPopupW) && mouseY >= (double)this.lastPopupY && mouseY <= (double)(this.lastPopupY + this.lastPopupH)) {
                this.showPopup = false;
                return true;
            }
            this.showPopup = false;
        }
        if (button == 0 && this.isOverHelpBtn(mouseX, mouseY)) {
            this.showPopup = !this.showPopup;
            if (this.showPopup) {
                activePopup = this;
            } else if (activePopup == this) {
                activePopup = null;
            }
            return true;
        }
        if (mouseX >= (double)this.x && mouseX <= (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY <= (double)(this.y + 14)) {
            if (button == 0) {
                if (!this.settingComponents.isEmpty() && mouseX >= (double)(this.x + this.width - 12)) {
                    this.expanded = !this.expanded;
                    return true;
                }
                this.module.toggle();
                return true;
            }
            if (button == 1) {
                this.listeningForBind = true;
                return true;
            }
        }
        if (this.expanded) {
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.mouseClicked(mouseX, mouseY, button)) continue;
                this.draggingComponent = comp;
                return true;
            }
        }
        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingComponent = null;
        for (SettingComponent comp : this.settingComponents) {
            comp.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.draggingComponent != null) {
            return this.draggingComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    public boolean keyPressed(int keyCode) {
        if (this.listeningForBind) {
            if (keyCode == 256 || keyCode == 261) {
                this.module.setKeyBind(-1);
            } else {
                this.module.setKeyBind(keyCode);
            }
            this.listeningForBind = false;
            return true;
        }
        return false;
    }
}
