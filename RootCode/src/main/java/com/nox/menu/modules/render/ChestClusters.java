package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.core.util.ClusterUtils;
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
        int playerChunkX = mc.player.chunkPosition().x();
        int playerChunkZ = mc.player.chunkPosition().z();
        int chunkRadius = (int) Math.ceil(this.radius.getValue() / 16.0) + 1;
        double radSq = this.radius.getValue() * this.radius.getValue();
        Vec3 playerPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        ArrayList<BlockPos> found = new ArrayList<BlockPos>();
        for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; ++cx) {
            for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; ++cz) {
                if (!mc.level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = mc.level.getChunk(cx, cz);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    boolean match = (this.chests.isEnabled() && be instanceof ChestBlockEntity)
                            || (this.traps.isEnabled() && be instanceof TrappedChestBlockEntity)
                            || (this.barrels.isEnabled() && be instanceof BarrelBlockEntity);
                    if (!match) continue;
                    BlockPos pos = be.getBlockPos();
                    double dx = pos.getX() + 0.5 - playerPos.x;
                    double dy = pos.getY() + 0.5 - playerPos.y;
                    double dz = pos.getZ() + 0.5 - playerPos.z;
                    if (dx * dx + dy * dy + dz * dz > radSq) continue;
                    found.add(pos);
                }
            }
        }
        this.clusters = ClusterUtils.clusterByProximity(found, this.threshold.getValue(), this.minSize.getIntValue(),
                pos -> new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<List<BlockPos>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();
        Vec3 forward = Vec3.directionFromRotation(camera.xRot(), camera.yRot()).scale(2.0);
        int lR = ColorUtil.red(COLOR_LINE);
        int lG = ColorUtil.green(COLOR_LINE);
        int lB = ColorUtil.blue(COLOR_LINE);
        int bR = ColorUtil.red(COLOR_BOX);
        int bG = ColorUtil.green(COLOR_BOX);
        int bBl = ColorUtil.blue(COLOR_BOX);

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();
            for (List<BlockPos> cluster : snapshot) {
                double cx = 0.0, cy = 0.0, cz = 0.0;
                double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
                double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
                for (BlockPos p : cluster) {
                    cx += p.getX() + 0.5;
                    cy += p.getY() + 0.5;
                    cz += p.getZ() + 0.5;
                    minX = Math.min(minX, p.getX());
                    minY = Math.min(minY, p.getY());
                    minZ = Math.min(minZ, p.getZ());
                    maxX = Math.max(maxX, p.getX() + 1.0);
                    maxY = Math.max(maxY, p.getY() + 1.0);
                    maxZ = Math.max(maxZ, p.getZ() + 1.0);
                }
                double relCx = (cx /= cluster.size()) - camPos.x;
                double relCy = (cy /= cluster.size()) - camPos.y;
                double relCz = (cz /= cluster.size()) - camPos.z;
                vc.addVertex(matrix, (float) forward.x, (float) forward.y, (float) forward.z).setColor(lR, lG, lB, 220).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                vc.addVertex(matrix, (float) relCx, (float) relCy, (float) relCz).setColor(lR, lG, lB, 220).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
                double margin = 1.0;
                AABB box = new AABB(minX - margin - camPos.x, minY - margin - camPos.y, minZ - margin - camPos.z,
                        maxX + margin - camPos.x, maxY + margin - camPos.y, maxZ + margin - camPos.z);
                this.drawBoxLines(vc, matrix, box, bR, bG, bBl, 200);
            }
        });
    }

    @Override
    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
        if (!this.nullCheck() || !this.showLabel.isEnabled()) {
            return;
        }
        List<List<BlockPos>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();
        for (List<BlockPos> cluster : snapshot) {
            double cx = 0.0, cy = 0.0, cz = 0.0;
            for (BlockPos p : cluster) {
                cx += p.getX() + 0.5;
                cy += p.getY() + 0.5;
                cz += p.getZ() + 0.5;
            }
            int[] screen = this.worldToScreen(cx / cluster.size(), cy / cluster.size(), cz / cluster.size(), camPos, camera);
            if (screen == null) continue;
            String label = "§6⊞ " + cluster.size() + " chests";
            int textW = mc.font.width(label);
            context.text(mc.font, label, screen[0] - textW / 2, screen[1] - 6, COLOR_LINE, true);
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

    private void drawBoxLines(VertexConsumer b, Matrix4f m, AABB box, int r, int g, int bl, int a) {
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z0).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y0, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        b.addVertex(m, x0, y1, z1).setColor(r, g, bl, a).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
    }
}
