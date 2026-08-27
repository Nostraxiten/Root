package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class ESP extends Module {
    private final BooleanSetting players  = new BooleanSetting("Players",  true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", true);
    private final BooleanSetting animals  = new BooleanSetting("Animals",  false);
    private final BooleanSetting selfESP  = new BooleanSetting("Self ESP", false);

    private final ColorSetting playerColor  = new ColorSetting("Player Color",  ColorUtil.ACCENT_PRIMARY);
    private final ColorSetting hostileColor = new ColorSetting("Hostile Color", -52429);
    private final ColorSetting animalColor  = new ColorSetting("Animal Color",  -13369549);
    private final ColorSetting selfColor    = new ColorSetting("Self Color",    -1);   // blanco

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
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = ESP.mc.gameRenderer.getCamera();
        Vec3d camPos  = camera.getCameraPos();
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Self ESP — only in 2nd/3rd person perspective
        if (this.selfESP.getValue() && mc.options.getPerspective().isFirstPerson() == false) {
            Entity p = ESP.mc.player;
            double sx = MathHelper.lerp((double) tickDelta, (double) p.lastRenderX, (double) p.getX()) - camPos.x;
            double sy = MathHelper.lerp((double) tickDelta, (double) p.lastRenderY, (double) p.getY()) - camPos.y;
            double sz = MathHelper.lerp((double) tickDelta, (double) p.lastRenderZ, (double) p.getZ()) - camPos.z;
            Box selfBox = p.getBoundingBox().offset(-p.getX(), -p.getY(), -p.getZ()).offset(sx, sy, sz);
            int sc = this.selfColor.getValue();
            drawBoxLines(builder, matrix, selfBox, ColorUtil.red(sc), ColorUtil.green(sc), ColorUtil.blue(sc), 255);
        }

        for (Entity entity : ESP.mc.world.getEntities()) {
            if (entity == ESP.mc.player) continue;
            if (!(entity instanceof LivingEntity) || !entity.isAlive()) continue;

            boolean isPlayer  = entity instanceof PlayerEntity;
            boolean isHostile = entity instanceof HostileEntity;
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

            double x = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderX, (double) entity.getX()) - camPos.x;
            double y = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderY, (double) entity.getY()) - camPos.y;
            double z = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderZ, (double) entity.getZ()) - camPos.z;
            Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ()).offset(x, y, z);
            drawBoxLines(builder, matrix, box, r, g, b, 255);
        }

        immediate.draw(RenderLayers.LINES);
    }

    private void drawBoxLines(VertexConsumer vc, Matrix4f m, Box b, int r, int g, int bl, int a) {
        float minX = (float) b.minX, minY = (float) b.minY, minZ = (float) b.minZ;
        float maxX = (float) b.maxX, maxY = (float) b.maxY, maxZ = (float) b.maxZ;
        float fr = r / 255f, fg = g / 255f, fb = bl / 255f, fa = a / 255f;

        // Bottom face
        line(vc, m, minX, minY, minZ, maxX, minY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, minZ, maxX, minY, maxZ, fr, fg, fb, fa);
        line(vc, m, maxX, minY, maxZ, minX, minY, maxZ, fr, fg, fb, fa);
        line(vc, m, minX, minY, maxZ, minX, minY, minZ, fr, fg, fb, fa);
        // Top face
        line(vc, m, minX, maxY, minZ, maxX, maxY, minZ, fr, fg, fb, fa);
        line(vc, m, maxX, maxY, minZ, maxX, maxY, maxZ, fr, fg, fb, fa);
        line(vc, m, maxX, maxY, maxZ, minX, maxY, maxZ, fr, fg, fb, fa);
        line(vc, m, minX, maxY, maxZ, minX, maxY, minZ, fr, fg, fb, fa);
        // Vertical edges
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
        vc.vertex(m, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz).lineWidth(1.0f);
        vc.vertex(m, x2, y2, z2).color(r, g, b, a).normal(nx, ny, nz).lineWidth(1.0f);
    }
}
