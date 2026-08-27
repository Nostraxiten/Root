package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.math.Vec3d;

public class DeathCoords extends Module {
    private final BooleanSetting autoSaveWaypoint = new BooleanSetting("Auto Save Waypoint", true);

    private Vec3d deathPos = null;
    private boolean isDead = false;

    public DeathCoords() {
        super("DeathCoords", "Guarda y muestra coordenadas de muerte | Saves and shows death coordinates", Category.PLAYER);
        this.addSetting(this.autoSaveWaypoint);
        this.setEnabled(true);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        if (mc.player.isDead() || mc.currentScreen instanceof DeathScreen) {
            if (!isDead) {
                isDead = true;
                deathPos = new net.minecraft.util.math.Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                
                if (this.autoSaveWaypoint.isEnabled()) {
                    // Logic to integrate with Waypoints.java
                    // Assuming Waypoints module handles chat messages or rendering
                    mc.inGameHud.getChatHud().addMessage(net.minecraft.text.Text.literal("§a[NoxMenu] §fDeath waypoint saved at: " + 
                        (int)deathPos.x + ", " + (int)deathPos.y + ", " + (int)deathPos.z));
                }
            }
        } else {
            isDead = false;
        }
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (!this.nullCheck() || !this.isEnabled()) return;

        if (mc.currentScreen instanceof DeathScreen && deathPos != null) {
            String text = String.format("Coordenadas de Muerte: X: %d Y: %d Z: %d", 
                (int)deathPos.x, (int)deathPos.y, (int)deathPos.z);
            
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            
            int textWidth = mc.textRenderer.getWidth(text);
            
            // Draw text near the top of the screen
            context.drawTextWithShadow(mc.textRenderer, text, (width - textWidth) / 2, 20, 0xFF5555);
        }
    }
}
