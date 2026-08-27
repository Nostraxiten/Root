/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.TrappedChestBlockEntity
 *  net.minecraft.world.chunk.WorldChunk
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.block.entity.BarrelBlockEntity
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  org.joml.Matrix4f
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class ChestClusters
extends Module {
    private final BooleanSetting chests = new BooleanSetting("Chests", true);
    private final BooleanSetting traps = new BooleanSetting("Traps", true);
    private final BooleanSetting barrels = new BooleanSetting("Barrels", false);
    private final NumberSetting radius = new NumberSetting("Radius", 20.0, 5.0, 64.0, 1.0);
    private final NumberSetting threshold = new NumberSetting("LinkDist", 6.0, 2.0, 20.0, 1.0);
    private final NumberSetting minSize = new NumberSetting("MinChests", 3.0, 1.0, 16.0, 1.0);
    private final BooleanSetting showLabel = new BooleanSetting("Label", true);
    private static final int COLOR_LINE = -22016;
    private static final int COLOR_BOX = -39424;
    private volatile List<List<BlockPos>> clusters = Collections.emptyList();
    private int tickCounter = 0;
    private static final int SCAN_INTERVAL = 15;

    public ChestClusters() {
        super("ChestClusters", "Agrupa cofres cercanos y dibuja bounding boxes. | Groups nearby chests and draws cluster bounding boxes.", Category.RENDER);
        this.addSetting(this.chests);
        this.addSetting(this.traps);
        this.addSetting(this.barrels);
        this.addSetting(this.radius);
        this.addSetting(this.threshold);
        this.addSetting(this.minSize);
        this.addSetting(this.showLabel);
    }

    @Override
    public void onEnable() {
        this.clusters = Collections.emptyList();
        this.tickCounter = 0;
    }

    @Override
    public void onDisable() {
        this.clusters = Collections.emptyList();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (++this.tickCounter < 15) {
            return;
        }
        this.tickCounter = 0;
        int playerChunkX = ChestClusters.mc.player.getChunkPos().x;
        int playerChunkZ = ChestClusters.mc.player.getChunkPos().z;
        int chunkRadius = (int)Math.ceil((Double)this.radius.getValue() / 16.0) + 1;
        double radSq = (Double)this.radius.getValue() * (Double)this.radius.getValue();
        Vec3d playerPos = new Vec3d(ChestClusters.mc.player.getX(), ChestClusters.mc.player.getY(), ChestClusters.mc.player.getZ());
        ArrayList<BlockPos> found = new ArrayList<BlockPos>();
        for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; ++cx) {
            for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; ++cz) {
                WorldChunk chunk;
                if (!ChestClusters.mc.world.isChunkLoaded(cx, cz) || (chunk = ChestClusters.mc.world.getChunk(cx, cz)) == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    double dz;
                    double dy;
                    BlockPos pos;
                    double dx;
                    boolean match = (Boolean)this.chests.getValue() != false && be instanceof ChestBlockEntity || (Boolean)this.traps.getValue() != false && be instanceof TrappedChestBlockEntity || (Boolean)this.barrels.getValue() != false && be instanceof BarrelBlockEntity;
                    if (!match || !((dx = (double)(pos = be.getPos()).getX() + 0.5 - playerPos.x) * dx + (dy = (double)pos.getY() + 0.5 - playerPos.y) * dy + (dz = (double)pos.getZ() + 0.5 - playerPos.z) * dz <= radSq)) continue;
                    found.add(pos);
                }
            }
        }
        this.clusters = com.nox.menu.core.util.ClusterUtils.clusterByProximity(found, (Double)this.threshold.getValue(), this.minSize.getIntValue(), pos -> new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<List<BlockPos>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = ChestClusters.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lineBuilder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Vec3d forward = Vec3d.fromPolar((float)camera.getPitch(), (float)camera.getYaw()).multiply(2.0);
        int lR = ColorUtil.red(-22016);
        int lG = ColorUtil.green(-22016);
        int lB = ColorUtil.blue(-22016);
        int bR = ColorUtil.red(-39424);
        int bG = ColorUtil.green(-39424);
        int bBl = ColorUtil.blue(-39424);
        for (List<BlockPos> cluster : snapshot) {
            double cx = 0.0;
            double cy = 0.0;
            double cz = 0.0;
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxX = -1.7976931348623157E308;
            double maxY = -1.7976931348623157E308;
            double maxZ = -1.7976931348623157E308;
            for (BlockPos p : cluster) {
                cx += (double)p.getX() + 0.5;
                cy += (double)p.getY() + 0.5;
                cz += (double)p.getZ() + 0.5;
                minX = Math.min(minX, (double)p.getX());
                minY = Math.min(minY, (double)p.getY());
                minZ = Math.min(minZ, (double)p.getZ());
                maxX = Math.max(maxX, (double)p.getX() + 1.0);
                maxY = Math.max(maxY, (double)p.getY() + 1.0);
                maxZ = Math.max(maxZ, (double)p.getZ() + 1.0);
            }
            double relCx = (cx /= (double)cluster.size()) - camPos.x;
            double relCy = (cy /= (double)cluster.size()) - camPos.y;
            double relCz = (cz /= (double)cluster.size()) - camPos.z;
            lineBuilder.vertex(matrix, (float)forward.x, (float)forward.y, (float)forward.z).color(lR, lG, lB, 220).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
            lineBuilder.vertex(matrix, (float)relCx, (float)relCy, (float)relCz).color(lR, lG, lB, 220).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
            double margin = 1.0;
            Box box = new Box(minX - margin - camPos.x, minY - margin - camPos.y, minZ - margin - camPos.z, maxX + margin - camPos.x, maxY + margin - camPos.y, maxZ + margin - camPos.z);
            this.drawBoxLines(lineBuilder, matrix, box, bR, bG, bBl, 200);
        }
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (!this.nullCheck() || !((Boolean)this.showLabel.getValue()).booleanValue()) {
            return;
        }
        List<List<BlockPos>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = ChestClusters.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        for (List<BlockPos> cluster : snapshot) {
            double cx = 0.0;
            double cy = 0.0;
            double cz = 0.0;
            for (BlockPos p : cluster) {
                cx += (double)p.getX() + 0.5;
                cy += (double)p.getY() + 0.5;
                cz += (double)p.getZ() + 0.5;
            }
            int[] screen = this.worldToScreen(cx /= (double)cluster.size(), cy /= (double)cluster.size(), cz /= (double)cluster.size(), camPos, camera);
            if (screen == null) continue;
            String label = "\u00a76\u229e " + cluster.size() + " chests";
            int textW = ChestClusters.mc.textRenderer.getWidth(label);
            context.drawTextWithShadow(ChestClusters.mc.textRenderer, label, screen[0] - textW / 2, screen[1] - 6, -22016);
        }
    }



    private int[] worldToScreen(double wx, double wy, double wz, Vec3d camPos, Camera cam) {
        float yaw = (float)Math.toRadians(cam.getYaw());
        float pitch = (float)Math.toRadians(cam.getPitch());
        float rx = (float)(wx - camPos.x);
        float ry = (float)(wy - camPos.y);
        float rz = (float)(wz - camPos.z);
        float cosY = (float)Math.cos(yaw);
        float sinY = (float)Math.sin(yaw);
        float x1 = rx * cosY - rz * sinY;
        float z1 = rx * sinY + rz * cosY;
        float cosP = (float)Math.cos(-pitch);
        float sinP = (float)Math.sin(-pitch);
        float y2 = ry * cosP - z1 * sinP;
        float z2 = ry * sinP + z1 * cosP;
        if (z2 >= -0.01f) {
            return null;
        }
        double fovRad = Math.toRadians(((Integer)ChestClusters.mc.options.getFov().getValue()).intValue());
        double aspect = (double)mc.getWindow().getWidth() / (double)mc.getWindow().getHeight();
        double scX = (double)x1 / ((double)(-z2) * Math.tan(fovRad / 2.0));
        double scY = (double)y2 / ((double)(-z2) * Math.tan(fovRad / 2.0) / aspect);
        int sX = (int)((scX + 1.0) / 2.0 * (double)mc.getWindow().getScaledWidth());
        int sY = (int)((1.0 - scY) / 2.0 * (double)mc.getWindow().getScaledHeight());
        return new int[]{sX, sY};
    }

    private void drawBoxLines(VertexConsumer b, Matrix4f m, Box box, int r, int g, int bl, int a) {
        float x0 = (float)box.minX;
        float y0 = (float)box.minY;
        float z0 = (float)box.minZ;
        float x1 = (float)box.maxX;
        float y1 = (float)box.maxY;
        float z1 = (float)box.maxZ;
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z0).color(r, g, bl, a).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z0).color(r, g, bl, a).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z1).color(r, g, bl, a).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z1).color(r, g, bl, a).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y0, z1).color(r, g, bl, a).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y0, z1).color(r, g, bl, a).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z1).color(r, g, bl, a).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z1).color(r, g, bl, a).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        b.vertex(m, x0, y0, z0).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z0).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z0).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z0).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y0, z1).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y0, z1).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        b.vertex(m, x0, y1, z1).color(r, g, bl, a).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
    }
}

