package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import org.joml.Matrix4f;

import java.util.Random;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class Chunks extends Module {

    private final NumberSetting radius   = new NumberSetting("Radius",        4,  1, 16, 1);
    private final BooleanSetting showSlime    = new BooleanSetting("Slime Chunks",  true);
    private final BooleanSetting showEntities = new BooleanSetting("Entity Count",  true);
    private final BooleanSetting flatMode     = new BooleanSetting("Flat Mode (F3+G style)", true);

    // Colors
    private static final int[] COL_BORDER  = {200, 200, 200}; // white-ish for normal chunk edges
    private static final int[] COL_SLIME   = {0, 255, 80};    // lime green
    private static final int[] COL_NO_SLIME = {80, 120, 255}; // blue for non-slime when slime mode on

    public Chunks() {
        super("Chunks", "Muestra bordes de chunks, slime chunks y conteo de entidades. | Shows chunk borders, slime chunks and entity counts.", Category.WORLD);
        this.addSetting(this.radius);
        this.addSetting(this.showSlime);
        this.addSetting(this.showEntities);
        this.addSetting(this.flatMode);
    }

    /** Vanilla algorithm for slime chunks (same as in-game, requires world seed). */
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

    /** Try to read the world seed. Returns 0 on multiplayer (seed hidden). */
    private long getWorldSeed() {
        try {
            if (mc.getServer() != null) {
                return mc.getServer().getOverworld().getSeed();
            }
        } catch (Exception ignored) {}
        return 0L;
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) return;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos  = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        Matrix4f mat  = matrices.peek().getPositionMatrix();

        int r   = this.radius.getIntValue();
        int pcx = mc.player.getChunkPos().x;
        int pcz = mc.player.getChunkPos().z;
        long seed = getWorldSeed();

        boolean doSlime    = this.showSlime.getValue();
        boolean doEntities = this.showEntities.getValue();
        boolean flat       = this.flatMode.getValue();

        // ── Entity count per chunk ──────────────────────────────────────────
        Map<ChunkPos, Integer> entityCount = new HashMap<>();
        if (doEntities) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity == mc.player) continue;
                ChunkPos cp = entity.getChunkPos();
                entityCount.merge(cp, 1, Integer::sum);
            }
        }

        // ── Build Y range ───────────────────────────────────────────────────
        // Flat mode: draw only at the player's eye level (like F3+G).
        // Full mode: draw from world bottom to top.
        double playerY = mc.player.getEyeY();
        double lineY   = playerY - camPos.y;

        double worldBottom = mc.world.getBottomY() - camPos.y;
        double worldTop    = (mc.world.getBottomY() + mc.world.getHeight()) - camPos.y;

        // ── Draw chunk borders ──────────────────────────────────────────────
        VertexConsumer vc = immediate.getBuffer(RenderLayers.LINES);

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
                    // F3+G style: one horizontal square at eye level
                    drawFlatChunk(vc, mat, minX, lineY, minZ, maxX, maxZ, red, green, blue, alpha);
                } else {
                    // Full pillars from world bottom to top
                    drawFullChunk(vc, mat, minX, worldBottom, minZ, maxX, worldTop, maxZ, red, green, blue, alpha);
                }
            }
        }

        // Flush lines BEFORE drawing text (text internally flushes immediate)
        immediate.draw(RenderLayers.LINES);

        // ── Draw labels on chunk centers ────────────────────────────────────
        for (int cx = pcx - r; cx <= pcx + r; cx++) {
            for (int cz = pcz - r; cz <= pcz + r; cz++) {
                ChunkPos cp   = new ChunkPos(cx, cz);
                boolean slime = doSlime && isSlimeChunk(seed, cx, cz);
                int count     = doEntities ? entityCount.getOrDefault(cp, 0) : -1;

                // Only show label if there's something to say
                if (!slime && count <= 0) continue;

                double centerX = cx * 16 + 8 - camPos.x;
                double centerZ = cz * 16 + 8 - camPos.z;
                double labelY  = playerY + 2.5 - camPos.y;

                matrices.push();
                matrices.translate(centerX, labelY, centerZ);
                matrices.multiply(mc.gameRenderer.getCamera().getRotation());
                matrices.scale(-0.025f, -0.025f, 0.025f);

                Matrix4f textMat = matrices.peek().getPositionMatrix();
                TextRenderer tr  = mc.textRenderer;

                // Line 1: "SLIME" or chunk coords
                String line1;
                int    line1Color;
                if (slime) {
                    line1 = "§aSlime";
                    line1Color = 0x00FF50;
                } else {
                    line1 = "§7[" + cx + ", " + cz + "]";
                    line1Color = 0xAAAAAA;
                }
                float w1 = tr.getWidth(line1) / 2f;
                tr.draw(line1, -w1, 0, line1Color, false, textMat, immediate,
                        TextRenderer.TextLayerType.SEE_THROUGH, 0x44000000, 15728880);

                // Line 2: entity count (if > 0)
                if (count > 0) {
                    String line2 = "§e" + count + " entities";
                    float w2 = tr.getWidth(line2) / 2f;
                    tr.draw(line2, -w2, 10, 0xFFFF55, false, textMat, immediate,
                            TextRenderer.TextLayerType.SEE_THROUGH, 0x44000000, 15728880);
                }

                matrices.pop();
            }
        }
    }

    // ── Flat square at a given Y (F3+G style) ──────────────────────────────
    private void drawFlatChunk(VertexConsumer vc, Matrix4f m,
                               double x1, double y, double z1,
                               double x2, double z2,
                               int r, int g, int b, int a) {
        // 4 edges of the square
        hLine(vc, m, x1, x2, y, z1, r, g, b, a); // north
        hLine(vc, m, x1, x2, y, z2, r, g, b, a); // south
        zLine(vc, m, x1, z1, z2, y, r, g, b, a); // west
        zLine(vc, m, x2, z1, z2, y, r, g, b, a); // east

        // Corner tick-marks going down 3 blocks for visibility (like vanilla F3+G)
        vLine(vc, m, x1, z1, y, y - 3, r, g, b, a);
        vLine(vc, m, x2, z1, y, y - 3, r, g, b, a);
        vLine(vc, m, x1, z2, y, y - 3, r, g, b, a);
        vLine(vc, m, x2, z2, y, y - 3, r, g, b, a);
    }

    // ── Full pillar from bottom to top ──────────────────────────────────────
    private void drawFullChunk(VertexConsumer vc, Matrix4f m,
                               double x1, double yMin, double z1,
                               double x2, double yMax, double z2,
                               int r, int g, int b, int a) {
        // Bottom ring
        hLine(vc, m, x1, x2, yMin, z1, r, g, b, a);
        hLine(vc, m, x1, x2, yMin, z2, r, g, b, a);
        zLine(vc, m, x1, z1, z2, yMin, r, g, b, a);
        zLine(vc, m, x2, z1, z2, yMin, r, g, b, a);
        // Top ring
        hLine(vc, m, x1, x2, yMax, z1, r, g, b, a);
        hLine(vc, m, x1, x2, yMax, z2, r, g, b, a);
        zLine(vc, m, x1, z1, z2, yMax, r, g, b, a);
        zLine(vc, m, x2, z1, z2, yMax, r, g, b, a);
        // 4 vertical edges
        vLine(vc, m, x1, z1, yMin, yMax, r, g, b, a);
        vLine(vc, m, x2, z1, yMin, yMax, r, g, b, a);
        vLine(vc, m, x1, z2, yMin, yMax, r, g, b, a);
        vLine(vc, m, x2, z2, yMin, yMax, r, g, b, a);
    }

    // ── Primitive line helpers ──────────────────────────────────────────────
    /** Horizontal line along X at fixed Y, Z */
    private void hLine(VertexConsumer vc, Matrix4f m, double x1, double x2, double y, double z, int r, int g, int b, int a) {
        seg(vc, m, x1, y, z, x2, y, z, r, g, b, a);
    }
    /** Horizontal line along Z at fixed Y, X */
    private void zLine(VertexConsumer vc, Matrix4f m, double x, double z1, double z2, double y, int r, int g, int b, int a) {
        seg(vc, m, x, y, z1, x, y, z2, r, g, b, a);
    }
    /** Vertical line at fixed X, Z from y1 to y2 */
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
        vc.vertex(m, (float)x1, (float)y1, (float)z1).color(r, g, b, a).normal(nx, ny, nz).lineWidth(1.0f);
        vc.vertex(m, (float)x2, (float)y2, (float)z2).color(r, g, b, a).normal(nx, ny, nz).lineWidth(1.0f);
    }
}
