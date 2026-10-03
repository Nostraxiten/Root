package com.nox.menu.modules.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class Chunks extends Module {

    private final NumberSetting radius   = new NumberSetting("Radius",        4,  1, 16, 1);
    private final BooleanSetting showSlime    = new BooleanSetting("Slime Chunks",  true);
    private final BooleanSetting showEntities = new BooleanSetting("Entity Count",  true);
    private final BooleanSetting flatMode     = new BooleanSetting("Flat Mode (F3+G style)", true);

    private static final int[] COL_BORDER  = {200, 200, 200};
    private static final int[] COL_SLIME   = {0, 255, 80};
    private static final int[] COL_NO_SLIME = {80, 120, 255};

    public Chunks() {
        super("Chunks", "Muestra bordes de chunks, slime chunks y conteo de entidades. | Shows chunk borders, slime chunks and entity counts.", Category.WORLD);
        this.addSetting(this.radius);
        this.addSetting(this.showSlime);
        this.addSetting(this.showEntities);
        this.addSetting(this.flatMode);
    }

    private boolean isSlimeChunk(long seed, int cx, int cz) {
        Random rng = new Random(
            seed
            + (long)(cx * cx * 4987142)
            + (long)(cx * 5947611)
            + (long)(cz * cz) * 4392871L
            + (long)(cz * 389711)
            ^ 987234911L
        );
        return rng.nextInt(10) == 0;
    }

    private long getWorldSeed() {
        try {
            if (mc.getSingleplayerServer() != null) {
                return mc.getSingleplayerServer().overworld().getSeed();
            }
        } catch (Exception ignored) {}
        return 0L;
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        int r   = this.radius.getIntValue();
        int pcx = mc.player.chunkPosition().x();
        int pcz = mc.player.chunkPosition().z();
        long seed = getWorldSeed();

        boolean doSlime    = this.showSlime.getValue();
        boolean flat       = this.flatMode.getValue();

        double playerY = mc.player.getEyeY();
        double lineY   = playerY - camPos.y;

        double worldBottom = mc.level.getMinY() - camPos.y;
        double worldTop    = (mc.level.getMinY() + mc.level.getHeight()) - camPos.y;

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f mat = pose.pose();
            for (int cx = pcx - r; cx <= pcx + r; cx++) {
                for (int cz = pcz - r; cz <= pcz + r; cz++) {
                    boolean slime = doSlime && isSlimeChunk(seed, cx, cz);

                    int[] col = slime ? COL_SLIME : (doSlime ? COL_NO_SLIME : COL_BORDER);
                    int red = col[0], green = col[1], blue = col[2];
                    int alpha = 255;

                    double minX = cx * 16 - camPos.x;
                    double minZ = cz * 16 - camPos.z;
                    double maxX = minX + 16;
                    double maxZ = minZ + 16;

                    if (flat) {
                        drawFlatChunk(vc, mat, minX, lineY, minZ, maxX, maxZ, red, green, blue, alpha);
                    } else {
                        drawFullChunk(vc, mat, minX, worldBottom, minZ, maxX, worldTop, maxZ, red, green, blue, alpha);
                    }
                }
            }
        });
    }

    @Override
    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
        if (!this.nullCheck()) return;

        boolean doSlime    = this.showSlime.getValue();
        boolean doEntities = this.showEntities.getValue();
        if (!doSlime && !doEntities) return;

        int r   = this.radius.getIntValue();
        int pcx = mc.player.chunkPosition().x();
        int pcz = mc.player.chunkPosition().z();
        long seed = getWorldSeed();

        Map<ChunkPos, Integer> entityCount = new HashMap<>();
        if (doEntities) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player) continue;
                ChunkPos cp = entity.chunkPosition();
                entityCount.merge(cp, 1, Integer::sum);
            }
        }

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();
        double playerY = mc.player.getEyeY();

        for (int cx = pcx - r; cx <= pcx + r; cx++) {
            for (int cz = pcz - r; cz <= pcz + r; cz++) {
                ChunkPos cp   = new ChunkPos(cx, cz);
                boolean slime = doSlime && isSlimeChunk(seed, cx, cz);
                int count     = doEntities ? entityCount.getOrDefault(cp, 0) : -1;

                if (!slime && count <= 0) continue;

                double centerX = cx * 16 + 8;
                double centerZ = cz * 16 + 8;
                int[] screen = this.worldToScreen(centerX, playerY, centerZ, camPos, camera);
                if (screen == null) continue;

                String line1 = slime ? "§aSlime" : "§7[" + cx + ", " + cz + "]";
                int line1Color = slime ? 0x00FF50 : 0xAAAAAA;
                int w1 = mc.font.width(line1);
                context.text(mc.font, line1, screen[0] - w1 / 2, screen[1] - 6, line1Color, true);

                if (count > 0) {
                    String line2 = "§e" + count + " entities";
                    int w2 = mc.font.width(line2);
                    context.text(mc.font, line2, screen[0] - w2 / 2, screen[1] + 4, 0xFFFF55, true);
                }
            }
        }
    }

    private int[] worldToScreen(double wx, double wy, double wz, Vec3 camPos, Camera cam) {
        float yaw = (float) Math.toRadians(cam.yRot());
        float pitch = (float) Math.toRadians(cam.xRot());
        float rx = (float) (wx - camPos.x);
        float ry = (float) (wy - camPos.y);
        float rz = (float) (wz - camPos.z);
        float cosY = (float) Math.cos(yaw);
        float sinY = (float) Math.sin(yaw);
        float x1 = rx * cosY - rz * sinY;
        float z1 = rx * sinY + rz * cosY;
        float cosP = (float) Math.cos(-pitch);
        float sinP = (float) Math.sin(-pitch);
        float y2 = ry * cosP - z1 * sinP;
        float z2 = ry * sinP + z1 * cosP;
        if (z2 >= -0.01f) {
            return null;
        }
        double fovRad = Math.toRadians(mc.options.fov().get());
        double aspect = (double) mc.getWindow().getWidth() / (double) mc.getWindow().getHeight();
        double scX = x1 / ((-z2) * Math.tan(fovRad / 2.0));
        double scY = y2 / ((-z2) * Math.tan(fovRad / 2.0) / aspect);
        int sX = (int) ((scX + 1.0) / 2.0 * mc.getWindow().getGuiScaledWidth());
        int sY = (int) ((1.0 - scY) / 2.0 * mc.getWindow().getGuiScaledHeight());
        return new int[]{sX, sY};
    }

    private void drawFlatChunk(VertexConsumer vc, Matrix4f m,
                               double x1, double y, double z1,
                               double x2, double z2,
                               int r, int g, int b, int a) {
        hLine(vc, m, x1, x2, y, z1, r, g, b, a);
        hLine(vc, m, x1, x2, y, z2, r, g, b, a);
        zLine(vc, m, x1, z1, z2, y, r, g, b, a);
        zLine(vc, m, x2, z1, z2, y, r, g, b, a);

        vLine(vc, m, x1, z1, y, y - 3, r, g, b, a);
        vLine(vc, m, x2, z1, y, y - 3, r, g, b, a);
        vLine(vc, m, x1, z2, y, y - 3, r, g, b, a);
        vLine(vc, m, x2, z2, y, y - 3, r, g, b, a);
    }

    private void drawFullChunk(VertexConsumer vc, Matrix4f m,
                               double x1, double yMin, double z1,
                               double x2, double yMax, double z2,
                               int r, int g, int b, int a) {
        hLine(vc, m, x1, x2, yMin, z1, r, g, b, a);
        hLine(vc, m, x1, x2, yMin, z2, r, g, b, a);
        zLine(vc, m, x1, z1, z2, yMin, r, g, b, a);
        zLine(vc, m, x2, z1, z2, yMin, r, g, b, a);
        hLine(vc, m, x1, x2, yMax, z1, r, g, b, a);
        hLine(vc, m, x1, x2, yMax, z2, r, g, b, a);
        zLine(vc, m, x1, z1, z2, yMax, r, g, b, a);
        zLine(vc, m, x2, z1, z2, yMax, r, g, b, a);
        vLine(vc, m, x1, z1, yMin, yMax, r, g, b, a);
        vLine(vc, m, x2, z1, yMin, yMax, r, g, b, a);
        vLine(vc, m, x1, z2, yMin, yMax, r, g, b, a);
        vLine(vc, m, x2, z2, yMin, yMax, r, g, b, a);
    }

    private void hLine(VertexConsumer vc, Matrix4f m, double x1, double x2, double y, double z, int r, int g, int b, int a) {
        seg(vc, m, x1, y, z, x2, y, z, r, g, b, a);
    }
    private void zLine(VertexConsumer vc, Matrix4f m, double x, double z1, double z2, double y, int r, int g, int b, int a) {
        seg(vc, m, x, y, z1, x, y, z2, r, g, b, a);
    }
    private void vLine(VertexConsumer vc, Matrix4f m, double x, double z, double y1, double y2, int r, int g, int b, int a) {
        seg(vc, m, x, y1, z, x, y2, z, r, g, b, a);
    }

    private void seg(VertexConsumer vc, Matrix4f m,
                     double x1, double y1, double z1,
                     double x2, double y2, double z2,
                     int r, int g, int b, int a) {
        float dx = (float)(x2 - x1), dy = (float)(y2 - y1), dz = (float)(z2 - z1);
        float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) return;
        float nx = dx/len, ny = dy/len, nz = dz/len;
        vc.addVertex(m, (float)x1, (float)y1, (float)z1).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
        vc.addVertex(m, (float)x2, (float)y2, (float)z2).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
    }
}
