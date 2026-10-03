/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.core.Holder
 *  net.minecraft.client.player.LocalPlayer
 */
package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.client.player.LocalPlayer;

public class Step
extends Module {
    private final NumberSetting height = new NumberSetting("Height", 1.0, 0.6, 2.5, 0.1);
    private float previousStepHeight;

    public Step() {
        super("Step", "Sube bloques completos instantaneamente sin saltar. | Step up full blocks instantly without jumping.", Category.MOVEMENT);
        this.addSetting(this.height);
    }

    private static Holder<Attribute> getStepHeightAttribute() {
        return Attributes.STEP_HEIGHT;
    }

    @Override
    public void onEnable() {
        AttributeInstance inst;
        if (!this.nullCheck()) {
            return;
        }
        Holder<Attribute> attr = Step.getStepHeightAttribute();
        if (attr != null && (inst = Step.mc.player.getAttribute(attr)) != null) {
            this.previousStepHeight = (float)inst.getBaseValue();
        }
    }

    @Override
    public void onDisable() {
        AttributeInstance inst;
        if (!this.nullCheck()) {
            return;
        }
        Holder<Attribute> attr = Step.getStepHeightAttribute();
        if (attr != null && (inst = Step.mc.player.getAttribute(attr)) != null) {
            inst.setBaseValue((double)this.previousStepHeight);
        }
    }

    public void applyStepHeight(LocalPlayer player) {
        AttributeInstance inst;
        Holder<Attribute> attr = Step.getStepHeightAttribute();
        if (attr != null && (inst = player.getAttribute(attr)) != null) {
            inst.setBaseValue((double)this.height.getFloatValue());
        }
    }
}

