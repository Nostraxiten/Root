package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ESP extends Module {
    private final BooleanSetting players  = new BooleanSetting("Players",  true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", true);
    private final BooleanSetting animals  = new BooleanSetting("Animals",  false);
    private final BooleanSetting selfESP  = new BooleanSetting("Self ESP", false);

    private final ColorSetting playerColor  = new ColorSetting("Player Color",  ColorUtil.ACCENT_PRIMARY);
    private final ColorSetting hostileColor = new ColorSetting("Hostile Color", -52429);
    private final ColorSetting animalColor  = new ColorSetting("Animal Color",  -13369549);
    private final ColorSetting selfColor    = new ColorSetting("Self Color",    -1);

    public ESP() {
        super("ESP", "Dibuja cajas alrededor de jugadores y mobs. | Draws boxes around players and mobs.", Category.RENDER);
        this.addSetting(this.players);
        this.addSetting(this.playerColor);
        this.addSetting(this.hostiles);
        this.addSetting(this.hostileColor);
        this.addSetting(this.animals);
        this.addSetting(this.animalColor);
        this.addSetting(this.selfESP);
        this.addSetting(this.selfColor);
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();

            if (this.selfESP.getValue() && !mc.options.getCameraType().isFirstPerson()) {
                Entity p = mc.player;
                Vec3 pos = p.getPosition(tickDelta).subtract(camPos);
                AABB selfBox = p.getBoundingBox().move(-p.getX(), -p.getY(), -p.getZ()).move(pos);
                int sc = this.selfColor.getValue();
                drawBoxLines(vc, matrix, selfBox, ColorUtil.red(sc), ColorUtil.green(sc), ColorUtil.blue(sc), 255);
            }

            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player) continue;
                if (!(entity instanceof LivingEntity) || !entity.isAlive()) continue;

                boolean isPlayer  = entity instanceof Player;
                boolean isHostile = entity instanceof Monster;
                boolean isAnimal  = !isPlayer && !isHostile;

                if (isPlayer  && !this.players.getValue())  continue;
                if (isHostile && !this.hostiles.getValue()) continue;
                if (isAnimal  && !this.animals.getValue())  continue;

                int color = isPlayer  ? this.playerColor.getValue()
                          : isHostile ? this.hostileColor.getValue()
                          :             this.animalColor.getValue();

                int r = ColorUtil.red(color);
                int g = ColorUtil.green(color);
                int b = ColorUtil.blue(color);

                Vec3 pos = entity.getPosition(tickDelta).subtract(camPos);
                AABB box = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ()).move(pos);
                drawBoxLines(vc, matrix, box, r, g, b, 255);
            }
        });
    }

    private void drawBoxLines(VertexConsumer vc, Matrix4f m, AABB b, int r, int g, int bl, int a) {
        float minX = (float) b.minX, minY = (float) b.minY, minZ = (float) b.minZ;
        float maxX = (float) b.maxX, maxY = (float) b.maxY, maxZ = (float) b.maxZ;
        float fr = r / 255f, fg = g / 255f, fb = bl / 255f, fa = a / 255f;

        line(vc, m, minX, minY, minZ, maxX, minY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, minZ, maxX, minY, maxZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, maxZ, minX, minY, maxZ, fr, fg, fb, fa);
        line(vc, m, minX, minY, maxZ, minX, minY, minZ, fr, fg, fb, fa);
        line(vc, m, minX, maxY, minZ, maxX, maxY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, maxY, minZ, maxX, maxY, maxZ, fr, fg, fb, fa);
        line(vc, m, maxX, maxY, maxZ, minX, maxY, maxZ, fr, fg, fb, fa);
        line(vc, m, minX, maxY, maxZ, minX, maxY, minZ, fr, fg, fb, fa);
        line(vc, m, minX, minY, minZ, minX, maxY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, minZ, maxX, maxY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, maxZ, maxX, maxY, maxZ, fr, fg, fb, fa);
        line(vc, m, minX, minY, maxZ, minX, maxY, maxZ, fr, fg, fb, fa);
    }

    private void line(VertexConsumer vc, Matrix4f m,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float r, float g, float b, float a) {
        float nx = x2 - x1, ny = y2 - y1, nz = z2 - z1;
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len == 0) return;
        nx /= len; ny /= len; nz /= len;
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
    }
}
