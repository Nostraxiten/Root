package com.nox.menu.gui;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.config.SettingsIO;
import com.nox.menu.gui.components.CategoryPanel;
import com.nox.menu.gui.components.SearchBar;
import com.nox.menu.gui.util.AnimationUtil;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGUIScreen
extends Screen {
    private final List<CategoryPanel> panels = new ArrayList<CategoryPanel>();
    private final SearchBar searchBar = new SearchBar(0, 5, 200, 18);
    private float animationProgress = 0.0f;
    private boolean initialized = false;

    public ClickGUIScreen() {
        super(Component.literal("Root"));
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

    @Override
    protected void init() {
        super.init();
        this.initPanels();
        this.animationProgress = 0.0f;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        this.animationProgress = AnimationUtil.animate(this.animationProgress, 1.0f, 0.18f);
        int overlayAlpha = (int)(this.animationProgress * 180.0f);
        context.fill(0, 0, this.width, this.height, ColorUtil.withAlpha(0, overlayAlpha));
        RenderUtil.drawHorizontalGradient(context, 0, 0, this.width, 25, ColorUtil.ACCENT_GRADIENT_START, ColorUtil.ACCENT_GRADIENT_END);
        RenderUtil.drawText(context, this.font, "Root", 15, 8, ColorUtil.TEXT_PRIMARY);
        this.searchBar.render(context, this.font, mouseX, mouseY);
        String filter = this.searchBar.getText().toLowerCase();
        for (CategoryPanel panel : this.panels) {
            panel.render(context, this.font, mouseX, mouseY, filter);
        }
    }

    // ── Mouse/keyboard handling ──────────────────────────────────────────────
    // 26.2 wraps mouse/key/char events into MouseButtonEvent/KeyEvent/CharacterEvent
    // records; we unwrap them here and keep the panel-level logic on plain
    // primitives, same as every other version of this GUI.

    private boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBar.mouseClicked(mouseX, mouseY, button)) {
            com.nox.menu.gui.components.ModuleButton.activeListeningButton = null;
            return true;
        }
        for (int i = this.panels.size() - 1; i >= 0; --i) {
            if (!this.panels.get(i).mouseClicked(mouseX, mouseY, button)) continue;
            CategoryPanel clicked = this.panels.remove(i);
            this.panels.add(clicked);
            return true;
        }
        com.nox.menu.gui.components.ModuleButton.activeListeningButton = null;
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

    public static boolean isEscapeKey(int key, int keycode) {
        return key == 41 || key == 256 || key == 27 || keycode == 27 || keycode == 256 || keycode == 41;
    }

    private boolean handleKeyPressed(int key, int keycode, int modifiers) {
        if (this.searchBar.keyPressed(key, keycode, modifiers)) {
            return true;
        }
        boolean hadActiveListening = com.nox.menu.gui.components.ModuleButton.activeListeningButton != null;
        for (CategoryPanel panel : this.panels) {
            if (!panel.keyPressed(key, keycode)) continue;
            ModuleManager manager = ModuleManager.getInstance();
            if (manager != null) manager.save();
            return true;
        }
        if (hadActiveListening) {
            return true;
        }
        if (isEscapeKey(key, keycode)) {
            this.onClose();
            return true;
        }
        return false;
    }

    private boolean handleCharTyped(char chr, int modifiers) {
        return this.searchBar.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        return handleMouseClicked(event.x(), event.y(), event.button());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return handleMouseReleased(event.x(), event.y(), event.button());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return handleMouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY);
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
    public boolean keyPressed(KeyEvent event) {
        return handleKeyPressed(event.key(), event.keycode(), event.modifiers());
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return handleCharTyped((char) event.codepoint(), 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        com.nox.menu.gui.components.ModuleButton.activeListeningButton = null;
        ModuleManager manager = ModuleManager.getInstance();
        if (manager != null) {
            manager.save();
        }
        SettingsIO.saveUI(this.panels);
        super.onClose();
    }
}
