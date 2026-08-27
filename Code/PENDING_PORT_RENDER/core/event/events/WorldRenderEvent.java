/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.nox.menu.core.event.events;

import com.nox.menu.core.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;

public class WorldRenderEvent
extends Event {
    private final MatrixStack matrices;
    private final float tickDelta;

    public WorldRenderEvent(MatrixStack matrices, float tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }
}

