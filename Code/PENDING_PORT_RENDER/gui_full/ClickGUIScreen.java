/*
 * ClickGUIScreen — adaptado a Minecraft 1.21.11 / Fabric API 0.141+
 *
 * Cambios respecto a la versión anterior:
 *  - push()/pop()/translate(x,y,z) eliminados (Matrix3x2fStack no los soporta).
 *    La animación de slide queda eliminada; el GUI aparece inmediatamente.
 *  - mouseClicked/Released/Dragged/keyPressed/charTyped ahora delegan
 *    directamente a la lógica propia sin llamar a super con la firma antigua,
 *    ya que en 1.21.11 el super espera objetos Click/KeyInput/CharInput.
 */
package com.nox.menu.gui;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.gui.components.CategoryPanel;
import com.nox.menu.gui.components.SearchBar;
import com.nox.menu.gui.util.AnimationUtil;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import com.nox.menu.core.config.SettingsIO;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.CharInput;

public class ClickGUIScreen
extends Screen {
    private final List<CategoryPanel> panels = new ArrayList<CategoryPanel>();
    private final SearchBar searchBar = new SearchBar(0, 5, 200, 18);
    private float animationProgress = 0.0f;
    private boolean initialized = false;

    public ClickGUIScreen() {
        super((Text)Text.literal((String)"Root"));
    }

    private void initPanels() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        this.panels.clear();
        int startX = 10;
        int startY = 30;
        int panelWidth = 150;
        int gap = 5;
        for (Category category : Category.values()) {
            List<Module> modules = manager.getModulesByCategory(category);
            CategoryPanel panel = new CategoryPanel(category, modules, startX, startY, panelWidth);
            this.panels.add(panel);
            startX += panelWidth + gap;
        }
        this.searchBar.setX((startX - gap) / 2 - this.searchBar.getWidth() / 2);
        SettingsIO.loadUI(this.panels);
    }

    protected void init() {
        super.init();
        this.initPanels();
        this.animationProgress = 0.0f;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.animationProgress = AnimationUtil.animate(this.animationProgress, 1.0f, 0.18f);
        int overlayAlpha = (int)(this.animationProgress * 180.0f);
        context.fill(0, 0, this.width, this.height, ColorUtil.withAlpha(0, overlayAlpha));
        // Slide animation removed — Matrix3x2fStack no tiene push()/pop()/translate(x,y,z)
        RenderUtil.drawHorizontalGradient(context, 0, 0, this.width, 25, ColorUtil.ACCENT_GRADIENT_START, ColorUtil.ACCENT_GRADIENT_END);
        RenderUtil.drawText(context, this.textRenderer, "Root", 15, 8, ColorUtil.TEXT_PRIMARY);
        this.searchBar.render(context, this.textRenderer, mouseX, mouseY);
        String filter = this.searchBar.getText().toLowerCase();
        for (CategoryPanel panel : this.panels) {
            panel.render(context, this.textRenderer, mouseX, mouseY, filter);
        }
        for (CategoryPanel panel : this.panels) {
            panel.renderPopups(context, this.textRenderer, mouseX, mouseY);
        }
    }

    // ── Mouse events ─────────────────────────────────────────────────────────
    // En 1.21.11 Screen recibe (Click, boolean) / (Click) / (Click, double, double).
    // Mantenemos la firma antigua para que el código de panels compile, pero
    // también sobreescribimos con la firma vacía correcta para que Minecraft
    // realmente llame a nuestro código.

    /** Compatibilidad interna — llamado desde nuestra propia lógica. */
    private boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (int i = this.panels.size() - 1; i >= 0; --i) {
            if (!this.panels.get(i).mouseClicked(mouseX, mouseY, button)) continue;
            CategoryPanel clicked = this.panels.remove(i);
            this.panels.add(clicked);
            return true;
        }
        return false;
    }

    private boolean handleMouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryPanel panel : this.panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        ModuleManager manager = ModuleManager.getInstance();
        if (manager != null) {
            manager.save();
        }
        return false;
    }

    private boolean handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (CategoryPanel panel : this.panels) {
            if (!panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) continue;
            return true;
        }
        return false;
    }

    private boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBar.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        for (CategoryPanel panel : this.panels) {
            if (!panel.keyPressed(keyCode)) continue;
            ModuleManager manager = ModuleManager.getInstance();
            if (manager != null) manager.save();
            return true;
        }
        if (keyCode == 256) {
            this.close();
            return true;
        }
        return false;
    }

    private boolean handleCharTyped(char chr, int modifiers) {
        return this.searchBar.charTyped(chr, modifiers);
    }

    // ── Overrides que Minecraft 1.21.11 realmente invoca ─────────────────────
    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        return handleMouseClicked(click.x(), click.y(), click.button());
    }

    @Override
    public boolean mouseReleased(Click click) {
        return handleMouseReleased(click.x(), click.y(), click.button());
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        return handleMouseDragged(click.x(), click.y(), click.button(), deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (CategoryPanel panel : this.panels) {
            if (!panel.mouseScrolled(mouseX, mouseY, verticalAmount)) continue;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        return handleKeyPressed(keyInput.key(), keyInput.scancode(), keyInput.modifiers());
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        return handleCharTyped((char)charInput.codepoint(), charInput.modifiers());
    }

    public boolean shouldPause() {
        return false;
    }

    public void close() {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager != null) {
            manager.save();
        }
        SettingsIO.saveUI(this.panels);
        super.close();
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }
}
