package com.nox.menu.core;

import com.nox.menu.core.Category;
import com.nox.menu.core.ModuleManager;
import com.nox.menu.core.setting.Setting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getInstance();
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keyBind;
    private final List<Setting<?>> settings = new ArrayList();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keyBind = -1;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
    }

    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public void setEnabled(boolean enabled) {
        this.setEnabled(enabled, false);
    }

    public void setEnabled(boolean enabled, boolean isLoad) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        ModuleManager moduleManager = ModuleManager.getInstance();
        if (moduleManager != null && !isLoad) {
            moduleManager.onModuleToggled(this);
        }
    }

    protected void addSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getKeyBind() {
        return this.keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    protected boolean nullCheck() {
        return Module.mc.player != null && Module.mc.level != null;
    }

    public String getInfoString() {
        return null;
    }
}
