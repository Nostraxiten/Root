package com.nox.menu.gui.components;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.gui.util.AnimationUtil;
import com.nox.menu.gui.util.ColorUtil;
import com.nox.menu.gui.util.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CategoryPanel {
    private final Category category;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    private int x;
    private int y;
    private final int width;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private double scrollOffset = 0.0;
    private double maxScroll = 0.0;
    private boolean collapsed = false;
    private float collapseAnimation = 1.0f;
    private String lastFilter = "";

    public CategoryPanel(Category category, List<Module> modules, int x, int y, int width) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        for (Module module : modules) {
            this.moduleButtons.add(new ModuleButton(module, width - 4));
        }
    }

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY, String filter) {
        this.lastFilter = filter;
        float targetCollapse = this.collapsed ? 0.0f : 1.0f;
        this.collapseAnimation = AnimationUtil.animate(this.collapseAnimation, targetCollapse, 0.2f);

        int contentStartY = this.y + 22;
        int totalContent = this.getContentHeight(filter);
        int visibleH = (int)((float)Math.min(350, totalContent) * this.collapseAnimation);

        // Outer scissor: clips the ENTIRE panel (header + content) to its own bounding box.
        // This prevents this panel from drawing over adjacent panels during overlap.
        int outerH = 22 + Math.max(0, visibleH);
        RenderUtil.enableScissor(context, this.x, this.y, this.width, outerH);

        RenderUtil.drawHorizontalGradient(context, this.x, this.y, this.width, 22, ColorUtil.ACCENT_GRADIENT_START, ColorUtil.ACCENT_GRADIENT_END);
        String headerText = this.category.getIcon() + " " + this.category.getDisplayName();
        RenderUtil.drawCenteredText(context, font, headerText, this.x + this.width / 2, this.y + 7, ColorUtil.TEXT_PRIMARY);
        String collapseIcon = this.collapsed ? "▸" : "▾";
        context.text(font, collapseIcon, this.x + this.width - 12, this.y + 7, ColorUtil.TEXT_SECONDARY, true);

        if (visibleH >= 1) {
            RenderUtil.drawRect(context, this.x, contentStartY, this.width, visibleH, ColorUtil.BACKGROUND);
            RenderUtil.drawOutline(context, this.x, contentStartY, this.width, visibleH, ColorUtil.SEPARATOR);

            // Inner scissor: also clips scroll area (intersects with outer scissor automatically)
            RenderUtil.enableScissor(context, this.x, contentStartY, this.width, visibleH);
            int currentY = contentStartY - (int)this.scrollOffset + 2;
            for (ModuleButton button : this.moduleButtons) {
                if (!filter.isEmpty() && !button.getModule().getName().toLowerCase().contains(filter)) continue;
                button.setPosition(this.x + 2, currentY);
                button.render(context, font, mouseX, mouseY);
                currentY += button.getHeight() + 1;
            }

            this.maxScroll = Math.max(0.0, (double)(currentY + (int)this.scrollOffset - contentStartY - visibleH));
            if (this.maxScroll > 0.0) {
                int scrollbarH = Math.max(10, (int)((double)visibleH / ((double)visibleH + this.maxScroll) * (double)visibleH));
                int scrollbarY = contentStartY + (int)(this.scrollOffset / this.maxScroll * (double)(visibleH - scrollbarH));
                RenderUtil.drawRect(context, this.x + this.width - 3, scrollbarY, 2, scrollbarH, ColorUtil.SCROLLBAR);
            }
            RenderUtil.disableScissor(context); // disable inner
        }

        RenderUtil.disableScissor(context); // disable outer
    }


    public void renderPopups(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        if (this.collapsed || this.collapseAnimation <= 0.01f) {
            return;
        }
        for (ModuleButton button : this.moduleButtons) {
            if (!this.lastFilter.isEmpty() && !button.getModule().getName().toLowerCase().contains(this.lastFilter)) continue;
            button.renderPopupIfOpen(context, font, mouseX, mouseY);
        }
    }

    private int getContentHeight(String filter) {
        int height = 4;
        for (ModuleButton button : this.moduleButtons) {
            if (!filter.isEmpty() && !button.getModule().getName().toLowerCase().contains(filter)) continue;
            height += button.getHeight() + 1;
        }
        return height;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isInHeader(mouseX, mouseY) && button == 0) {
            if (mouseX >= (double)(this.x + this.width - 15)) {
                this.collapsed = !this.collapsed;
                return true;
            }
            this.dragging = true;
            this.dragOffsetX = (int)(mouseX - (double)this.x);
            this.dragOffsetY = (int)(mouseY - (double)this.y);
            return true;
        }
        if (this.collapsed) {
            return false;
        }
        for (ModuleButton mb : this.moduleButtons) {
            if (!mb.mouseClicked(mouseX, mouseY, button)) continue;
            return true;
        }
        return this.isInPanel(mouseX, mouseY);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        for (ModuleButton mb : this.moduleButtons) {
            mb.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.dragging) {
            this.x = (int)(mouseX - (double)this.dragOffsetX);
            this.y = (int)(mouseY - (double)this.dragOffsetY);
            return true;
        }
        for (ModuleButton mb : this.moduleButtons) {
            if (!mb.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) continue;
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.isInPanel(mouseX, mouseY) && !this.collapsed) {
            this.scrollOffset = Math.max(0.0, Math.min(this.maxScroll, this.scrollOffset - amount * 10.0));
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode) {
        for (ModuleButton mb : this.moduleButtons) {
            if (!mb.keyPressed(keyCode)) continue;
            return true;
        }
        return false;
    }

    private boolean isInHeader(double mx, double my) {
        return mx >= (double)this.x && mx <= (double)(this.x + this.width) && my >= (double)this.y && my <= (double)(this.y + 22);
    }

    private boolean isInPanel(double mx, double my) {
        return mx >= (double)this.x && mx <= (double)(this.x + this.width) && my >= (double)this.y && my <= (double)(this.y + 22 + Math.min(350, this.getContentHeight(this.lastFilter)));
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Category getCategory() {
        return this.category;
    }

    public int getWidth() {
        return this.width;
    }
}
