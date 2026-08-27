package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.ColorSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.gui.util.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class Tracers extends Module {

    private final BooleanSetting players  = new BooleanSetting("Players",  true);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles", true);
    private final BooleanSetting animals  = new BooleanSetting("Animals",  false);

    private final ColorSetting playerColor  = new ColorSetting("Player Color",  ColorUtil.ACCENT_PRIMARY);
    private final ColorSetting hostileColor = new ColorSetting("Hostile Color", -52429);
    private final ColorSetting animalColor  = new ColorSetting("Animal Color",  -13369549);

    // Max distance — 0 means unlimited
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
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = Tracers.mc.gameRenderer.getCamera();
        Vec3d camPos  = camera.getCameraPos();

        // Origin of the line: slightly in front of the camera (crosshair tip)
        Vec3d forward = Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(0.5);
        float ox = (float) forward.x;
        float oy = (float) forward.y;
        float oz = (float) forward.z;

        VertexConsumerProvider.Immediate immediate = Tracers.mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vc  = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix    = matrices.peek().getPositionMatrix();

        double maxD = this.maxDist.getValue();

        for (Entity entity : Tracers.mc.world.getEntities()) {
            if (entity == Tracers.mc.player) continue;
            if (!(entity instanceof LivingEntity) || !entity.isAlive()) continue;

            boolean isPlayer  = entity instanceof PlayerEntity;
            boolean isHostile = entity instanceof HostileEntity;
            boolean isAnimal  = !isPlayer && !isHostile;

            if (isPlayer  && !this.players.getValue())  continue;
            if (isHostile && !this.hostiles.getValue()) continue;
            if (isAnimal  && !this.animals.getValue())  continue;

            // Interpolated entity position (center of entity)
            double ex = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderX, (double) entity.getX()) - camPos.x;
            double ey = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderY, (double) entity.getY()) - camPos.y
                        + entity.getHeight() * 0.5;
            double ez = MathHelper.lerp((double) tickDelta, (double) entity.lastRenderZ, (double) entity.getZ()) - camPos.z;

            // Distance check (squared for efficiency)
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

            // Compute normal vector for the line
            float dx = (float)(ex - ox), dy = (float)(ey - oy), dz = (float)(ez - oz);
            float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (len == 0) continue;
            float nx = dx/len, ny = dy/len, nz = dz/len;

            vc.vertex(matrix, ox, oy, oz)            .color(r, g, b, 1f).normal(nx, ny, nz).lineWidth(1.0f);
            vc.vertex(matrix, (float)ex, (float)ey, (float)ez).color(r, g, b, 1f).normal(nx, ny, nz).lineWidth(1.0f);
        }

        // CRITICAL: flush the buffer so lines actually appear
        immediate.draw(RenderLayers.LINES);
    }
}
