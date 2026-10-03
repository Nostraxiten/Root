package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class Tracers extends Module {

    private final BooleanSetting players  = new BooleanSetting("Players",  true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", true);
    private final BooleanSetting animals  = new BooleanSetting("Animals",  false);

    private final ColorSetting playerColor  = new ColorSetting("Player Color",  ColorUtil.ACCENT_PRIMARY);
    private final ColorSetting hostileColor = new ColorSetting("Hostile Color", -52429);
    private final ColorSetting animalColor  = new ColorSetting("Animal Color",  -13369549);

    private final NumberSetting maxDist = new NumberSetting("Max Distance", 0, 0, 256, 8);

    public Tracers() {
        super("Tracers", "Dibuja lineas desde la mira a entidades cercanas. | Draws lines from your crosshair to nearby entities.", Category.RENDER);
        this.addSetting(this.players);
        this.addSetting(this.playerColor);
        this.addSetting(this.hostiles);
        this.addSetting(this.hostileColor);
        this.addSetting(this.animals);
        this.addSetting(this.animalColor);
        this.addSetting(this.maxDist);
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        Vec3 forward = Vec3.directionFromRotation(camera.xRot(), camera.yRot()).scale(0.5);
        float ox = (float) forward.x;
        float oy = (float) forward.y;
        float oz = (float) forward.z;

        double maxD = this.maxDist.getValue();

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();

            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player) continue;
                if (!(entity instanceof LivingEntity) || !entity.isAlive()) continue;

                boolean isPlayer  = entity instanceof Player;
                boolean isHostile = entity instanceof Monster;
                boolean isAnimal  = !isPlayer && !isHostile;

                if (isPlayer  && !this.players.getValue())  continue;
                if (isHostile && !this.hostiles.getValue()) continue;
                if (isAnimal  && !this.animals.getValue())  continue;

                Vec3 entityPos = entity.getPosition(tickDelta).subtract(camPos);
                double ex = entityPos.x;
                double ey = entityPos.y + entity.getBbHeight() * 0.5;
                double ez = entityPos.z;

                if (maxD > 0) {
                    double distSq = ex * ex + ey * ey + ez * ez;
                    if (distSq > maxD * maxD) continue;
                }

                int color = isPlayer  ? this.playerColor.getValue()
                          : isHostile ? this.hostileColor.getValue()
                          :             this.animalColor.getValue();

                float r = ColorUtil.red(color)   / 255f;
                float g = ColorUtil.green(color) / 255f;
                float b = ColorUtil.blue(color)  / 255f;

                float dx = (float)(ex - ox), dy = (float)(ey - oy), dz = (float)(ez - oz);
                float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
                if (len == 0) continue;
                float nx = dx/len, ny = dy/len, nz = dz/len;

                vc.addVertex(matrix, ox, oy, oz).setColor(r, g, b, 1f).setNormal(nx, ny, nz).setLineWidth(1.0f);
                vc.addVertex(matrix, (float)ex, (float)ey, (float)ez).setColor(r, g, b, 1f).setNormal(nx, ny, nz).setLineWidth(1.0f);
            }
        });
    }
}
