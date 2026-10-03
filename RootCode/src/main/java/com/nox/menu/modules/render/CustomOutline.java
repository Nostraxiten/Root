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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public class CustomOutline extends Module {
    public final ColorSetting color = new ColorSetting("Color", 0xFF00E5FF);
    public final NumberSetting width = new NumberSetting("Width", 2.0, 1.0, 5.0, 0.5);
    public final BooleanSetting fill = new BooleanSetting("Fill", true);
    public final NumberSetting fillAlpha = new NumberSetting("Fill Alpha", 40.0, 0.0, 255.0, 5.0);
    public final BooleanSetting rainbow = new BooleanSetting("Rainbow", false);

    public CustomOutline() {
        super("CustomOutline", "Modifica el color y estilo del borde del bloque seleccionado | Customizes targeted block outline", Category.RENDER);
        this.addSetting(this.color);
        this.addSetting(this.width);
        this.addSetting(this.fill);
        this.addSetting(this.fillAlpha);
        this.addSetting(this.rainbow);
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck() || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (state.isAir()) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        int targetColor = this.color.getValue();
        if (this.rainbow.isEnabled()) {
            float hue = (System.currentTimeMillis() % 4000L) / 4000.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.85f, 1.0f);
            targetColor = (0xFF << 24) | (rgb & 0xFFFFFF);
        }

        final int r = ColorUtil.red(targetColor);
        final int g = ColorUtil.green(targetColor);
        final int b = ColorUtil.blue(targetColor);
        final int lineAlpha = 255;
        final float lineWidth = this.width.getFloatValue();

        VoxelShape shape = state.getShape(mc.level, pos);
        AABB localBox = shape.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : shape.bounds();

        double minX = pos.getX() + localBox.minX - camPos.x;
        double minY = pos.getY() + localBox.minY - camPos.y;
        double minZ = pos.getZ() + localBox.minZ - camPos.z;
        double maxX = pos.getX() + localBox.maxX - camPos.x;
        double maxY = pos.getY() + localBox.maxY - camPos.y;
        double maxZ = pos.getZ() + localBox.maxZ - camPos.z;

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        // Render translucent fill faces
        if (this.fill.isEnabled() && this.fillAlpha.getIntValue() > 0) {
            final int fAlpha = this.fillAlpha.getIntValue();
            collector.submitCustomGeometry(matrices, RenderTypes.debugFilledBox(), (pose, vc) -> {
                drawFilledBox(vc, pose.pose(), box, r, g, b, fAlpha);
            });
        }

        // Render outline lines
        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            drawBoxLines(vc, pose.pose(), box, r, g, b, lineAlpha, lineWidth);
        });
    }

    private void drawBoxLines(VertexConsumer b, Matrix4f m, AABB box, int r, int g, int bl, int a, float lw) {
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;

        // Bottom quad
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(lw);
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(lw);

        // Top quad
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(lw);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(lw);

        // Vertical pillars
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(lw);
    }

    private void drawFilledBox(VertexConsumer b, Matrix4f m, AABB box, int r, int g, int bl, int a) {
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;

        // Down face (Y-)
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a);

        // Up face (Y+)
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a);

        // North face (Z-)
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a);

        // South face (Z+)
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a);

        // West face (X-)
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a);

        // East face (X+)
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a);
    }
}
