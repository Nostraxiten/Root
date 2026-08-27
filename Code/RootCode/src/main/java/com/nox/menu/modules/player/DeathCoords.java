package com.nox.menu.modules.player;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class DeathCoords extends Module {
    private final BooleanSetting autoSaveWaypoint = new BooleanSetting("Auto Save Waypoint", true);

    private Vec3 deathPos = null;
    private boolean isDead = false;

    public DeathCoords() {
        super("DeathCoords", "Guarda y muestra coordenadas de muerte | Saves and shows death coordinates", Category.PLAYER);
        this.addSetting(this.autoSaveWaypoint);
        this.setEnabled(true);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        if (mc.player.isDeadOrDying() || mc.gui.screen() instanceof DeathScreen) {
            if (!isDead) {
                isDead = true;
                deathPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());

                if (this.autoSaveWaypoint.isEnabled()) {
                    mc.player.sendSystemMessage(Component.literal("§a[NoxMenu] §fDeath waypoint saved at: " +
                        (int) deathPos.x + ", " + (int) deathPos.y + ", " + (int) deathPos.z));
                }
            }
        } else {
            isDead = false;
        }
    }

    @Override
    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
        if (!this.nullCheck() || !this.isEnabled()) return;

        if (mc.gui.screen() instanceof DeathScreen && deathPos != null) {
            String text = String.format("Coordenadas de Muerte: X: %d Y: %d Z: %d",
                (int) deathPos.x, (int) deathPos.y, (int) deathPos.z);

            int width = mc.getWindow().getGuiScaledWidth();

            int textWidth = mc.font.width(text);

            context.text(mc.font, text, (width - textWidth) / 2, 20, 0xFF5555, true);
        }
    }
}
