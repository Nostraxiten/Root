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
import com.mojang.blaze3d.platform.InputConstants;

public class ModuleButton {
    private final Module module;
    private int width;
    private int x;
    private int y;

    public void setWidth(int w) { this.width = w; }
    public int  getWidth()      { return this.width; }

    private static final int HEADER_H = 14;
    private boolean expanded = false;
    private float expandAnimation = 0.0f;
    private final List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();

    /** Solo UN botón puede estar escuchando tecla al mismo tiempo. */
    public static ModuleButton activeListeningButton = null;

    private boolean hovered = false;
    private SettingComponent draggingComponent = null;

    public ModuleButton(Module module, int width) {
        this.module = module;
        this.width = width;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting bs) {
                this.settingComponents.add(new BooleanComponent(bs, width - 8));
            } else if (setting instanceof NumberSetting ns) {
                this.settingComponents.add(new SliderComponent(ns, width - 8));
            } else if (setting instanceof ModeSetting ms) {
                this.settingComponents.add(new ModeComponent(ms, width - 8));
            } else if (setting instanceof ColorSetting cs) {
                this.settingComponents.add(new ColorComponent(cs, width - 8));
            }
        }
    }

    public Module getModule() { return this.module; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isListening() { return activeListeningButton == this; }

    public static String getKeyName(int keyCode) {
        if (keyCode <= 0) return "";
        try {
            String name = InputConstants.Type.KEYBOARD.getOrCreate(keyCode).getDisplayName().getString();
            if (name != null && !name.isEmpty()) return name;
        } catch (Exception ignored) {}
        return String.valueOf(keyCode);
    }

    public int getHeight() {
        int settingsHeight = 0;
        if (this.expanded || this.expandAnimation > 0.01f) {
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                settingsHeight += comp.getHeight() + 1;
            }
        }
        return HEADER_H + (int)((float)settingsHeight * this.expandAnimation);
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseX <= this.x + this.width
            && mouseY >= this.y && mouseY < this.y + HEADER_H;

        float target = this.expanded ? 1.0f : 0.0f;
        this.expandAnimation = AnimationUtil.animate(this.expandAnimation, target, 0.2f);

        // Fondo del header
        int bgColor = this.hovered ? ColorUtil.withAlpha(ColorUtil.HOVER, 48) : 0;
        if (this.module.isEnabled()) {
            bgColor = ColorUtil.withAlpha(ColorUtil.ACCENT_PRIMARY, this.hovered ? 80 : 48);
        }
        RenderUtil.drawRect(context, this.x, this.y, this.width, HEADER_H, bgColor);

        // Indicador de estado (cuadradito de color)
        int dotColor = this.module.isEnabled() ? ColorUtil.MODULE_ENABLED : ColorUtil.MODULE_DISABLED;
        RenderUtil.drawRect(context, this.x + 2, this.y + 4, 4, 6, dotColor);

        // Nombre del módulo
        int nameColor = this.module.isEnabled() ? ColorUtil.TEXT_PRIMARY : ColorUtil.TEXT_SECONDARY;
        context.text(font, this.module.getName(), this.x + 9, this.y + 3, nameColor, true);

        // Indicador derecho: Settings (▾/▸) y Tecla ([KEY] o [...])
        int rightOffset = this.x + this.width - 4;

        // Flecha de settings si tiene sub-opciones
        if (!this.settingComponents.isEmpty()) {
            String arrow = this.expanded ? "▾" : "▸";
            int arrowColor = this.expanded ? ColorUtil.ACCENT_PRIMARY : ColorUtil.TEXT_SECONDARY;
            int arrowW = font.width(arrow);
            rightOffset -= arrowW;
            context.text(font, arrow, rightOffset, this.y + 3, arrowColor, true);
            rightOffset -= 3;
        }

        // Badge de tecla asignada o escuchando
        if (this.isListening()) {
            String listenStr = "[...]";
            int lw = font.width(listenStr);
            rightOffset -= lw;
            context.text(font, listenStr, rightOffset, this.y + 3, ColorUtil.ACCENT_SECONDARY, true);
        } else if (this.module.getKeyBind() > 0) {
            String keyStr = "[" + getKeyName(this.module.getKeyBind()).toUpperCase() + "]";
            int kw = font.width(keyStr);
            rightOffset -= kw;
            context.text(font, keyStr, rightOffset, this.y + 3, ColorUtil.TEXT_SECONDARY, true);
        }

        // Sub-opciones (con animación de expansión)
        if (this.expandAnimation > 0.01f) {
            int settingY = this.y + HEADER_H;
            int totalH = 0;
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                totalH += comp.getHeight() + 1;
            }
            RenderUtil.drawRect(context, this.x + 2, settingY, this.width - 4,
                (int)(totalH * this.expandAnimation), ColorUtil.BACKGROUND_DARKER);
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
                comp.setPosition(this.x + 4, settingY);
                comp.render(context, font, mouseX, mouseY);
                settingY += comp.getHeight() + 1;
            }
        }
    }

    private boolean isLeftClick(int button) {
        return button == 1 || button == 0;
    }

    private boolean isRightClick(int button) {
        return button == 3;
    }

    private boolean isMiddleClick(int button) {
        return button == 2;
    }

    // ── Eventos de ratón ──────────────────────────────────────────────────────

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inHeader = mouseX >= this.x && mouseX <= this.x + this.width
            && mouseY >= this.y && mouseY < this.y + HEADER_H;

        if (inHeader) {
            if (isLeftClick(button)) {
                // ── Click Izquierdo: Activar / Desactivar opción ────────────────
                if (activeListeningButton != null) activeListeningButton = null;
                this.module.toggle();
                return true;

            } else if (isRightClick(button)) {
                // ── Click Derecho: Desplegar / Plegar opciones ────────────────
                if (!this.settingComponents.isEmpty()) {
                    this.expanded = !this.expanded;
                }
                return true;

            } else if (isMiddleClick(button)) {
                // ── Click Rueda / Central: Configurar tecla ────────────────────
                activeListeningButton = (activeListeningButton == this) ? null : this;
                return true;
            }
        }

        // ── Sub-opciones (área por DEBAJO del header cuando está expandido) ───
        if (this.expanded && mouseY >= this.y + HEADER_H) {
            for (SettingComponent comp : this.settingComponents) {
                if (!comp.getSetting().isVisible()) continue;
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

    public static boolean isEscapeKey(int key, int keycode) {
        return key == 41 || key == 256 || key == 27 || keycode == 27 || keycode == 256 || keycode == 41;
    }

    public static boolean isBackspaceKey(int key, int keycode) {
        return key == 42 || key == 259 || key == 8 || keycode == 8 || keycode == 259 || keycode == 42;
    }

    public static boolean isDeleteKey(int key, int keycode) {
        return key == 76 || key == 261 || key == 127 || keycode == 127 || keycode == 261 || keycode == 76;
    }

    // ── Teclado (para asignar tecla) ──────────────────────────────────────────

    public boolean keyPressed(int key, int keycode) {
        if (activeListeningButton == this) {
            if (isEscapeKey(key, keycode) || isDeleteKey(key, keycode) || isBackspaceKey(key, keycode)) {
                // ESC, Delete o Backspace = borrar/quitar tecla asignada
                this.module.setKeyBind(-1);
                activeListeningButton = null;
            } else {
                // Asignar nueva tecla
                int assigned = key > 0 ? key : keycode;
                this.module.setKeyBind(assigned);
                activeListeningButton = null;
            }
            return true;
        }

        // Si el ratón está encima del botón y se pulsa Delete o Backspace, quitar tecla
        if (this.hovered && (isDeleteKey(key, keycode) || isBackspaceKey(key, keycode))) {
            this.module.setKeyBind(-1);
            return true;
        }

        return false;
    }
}

