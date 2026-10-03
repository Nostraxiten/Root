/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core;

public enum Category {
    COMBAT("Combat", "\u2694"),
    MOVEMENT("Movement", "\u27a1"),
    RENDER("Render", "\ud83d\udc41"),
    WORLD("World", "\u26cf"),
    PLAYER("Player", "\u263a"),
    OPTIMIZE("Optimize", "\u26a1"),
    THEME("Theme", "\ud83c\udfa8");

    private final String displayName;
    private final String icon;

    private Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getIcon() {
        return this.icon;
    }
}

