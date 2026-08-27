/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;

public class NotificationSystem
extends Module {
    private final NumberSetting duration = new NumberSetting("Duration (s)", 2.0, 1.0, 5.0, 0.5);
    private final List<Notification> notifications = new ArrayList<Notification>();

    public NotificationSystem() {
        super("Notifications", "Muestra notificaciones elegantes en pantalla. | Shows elegant on-screen notifications.", Category.PLAYER);
        this.addSetting(this.duration);
    }

    public void pushNotification(String message) {
        if (!this.isEnabled()) {
            return;
        }
        this.notifications.add(new Notification(message, this.duration.getFloatValue()));
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (this.notifications.isEmpty()) {
            return;
        }
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        int yOffset = screenHeight - 30;
        for (int i = this.notifications.size() - 1; i >= 0; --i) {
            Notification notif = this.notifications.get(i);
            notif.timeLeft -= mc.getRenderTickCounter().getDynamicDeltaTicks() / 20.0f;
            if (notif.timeLeft <= 0.0f) {
                this.notifications.remove(i);
                continue;
            }
            float alpha = 1.0f;
            if (notif.timeLeft < 0.5f) {
                alpha = notif.timeLeft / 0.5f;
            } else if (notif.timeLeft > this.duration.getFloatValue() - 0.2f) {
                alpha = (this.duration.getFloatValue() - notif.timeLeft) / 0.2f;
            }
            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            int textWidth = NotificationSystem.mc.textRenderer.getWidth(notif.message);
            int padding = 6;
            int width = textWidth + padding * 2;
            int height = 16;
            int x = screenWidth - width - 10;
            if (alpha < 1.0f && notif.timeLeft > this.duration.getFloatValue() - 0.2f) {
                x += (int)((1.0f - alpha) * (float)width);
            }
            int argbAlpha = (int)(alpha * 255.0f);
            int bgColor = argbAlpha << 24 | 0x1A1A2E;
            int textColor = argbAlpha << 24 | 0xFFFFFF;
            context.fill(x, yOffset, x + width, yOffset + height, bgColor);
            context.fill(x, yOffset, x + 2, yOffset + height, argbAlpha << 24 | 0x7C3AED);
            context.drawTextWithShadow(NotificationSystem.mc.textRenderer, notif.message, x + padding, yOffset + 4, textColor);
            yOffset -= height + 4;
        }
    }

    private static class Notification {
        String message;
        float timeLeft;

        Notification(String message, float duration) {
            this.message = message;
            this.timeLeft = duration;
        }
    }
}

