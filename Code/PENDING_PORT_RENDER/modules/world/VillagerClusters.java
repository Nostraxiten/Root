package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.core.util.ClusterUtils;
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class VillagerClusters
extends Module {
    private final NumberSetting scanRadius = new NumberSetting("scanRadius", 100.0, 10.0, 500.0, 10.0);
    private final NumberSetting clusterDistance = new NumberSetting("clusterDistance", 10.0, 2.0, 50.0, 1.0);
    private final NumberSetting minClusterSize = new NumberSetting("minClusterSize", 3.0, 1.0, 20.0, 1.0);
    private final NumberSetting scanInterval = new NumberSetting("scanInterval", 20.0, 1.0, 100.0, 1.0);
    private final BooleanSetting includeZombieVillagers = new BooleanSetting("includeZombie", true);
    private static final int COLOR_LINE = -16711936;
    private static final int COLOR_BOX = -16711936;
    private volatile List<List<Entity>> clusters = Collections.emptyList();
    private int tickCounter = 0;

    public VillagerClusters() {
        super("VillagerClusters", "Muestra clusters de aldeanos cercanos | Shows clusters of nearby villagers", Category.WORLD);
        this.addSetting(this.scanRadius);
        this.addSetting(this.clusterDistance);
        this.addSetting(this.minClusterSize);
        this.addSetting(this.scanInterval);
        this.addSetting(this.includeZombieVillagers);
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
        if (++this.tickCounter < this.scanInterval.getIntValue()) {
            return;
        }
        this.tickCounter = 0;
        double radSq = (Double)this.scanRadius.getValue() * (Double)this.scanRadius.getValue();
        Vec3d playerPos = new Vec3d(VillagerClusters.mc.player.getX(), VillagerClusters.mc.player.getY(), VillagerClusters.mc.player.getZ());
        ArrayList<Entity> found = new ArrayList<Entity>();
        
        for (Entity entity : VillagerClusters.mc.world.getEntities()) {
            if (entity instanceof VillagerEntity || ((Boolean)this.includeZombieVillagers.getValue() && entity instanceof ZombieVillagerEntity)) {
                if (entity.squaredDistanceTo(playerPos) <= radSq) {
                    found.add(entity);
                }
            }
        }
        this.clusters = ClusterUtils.clusterByProximity(found, (Double)this.clusterDistance.getValue(), this.minClusterSize.getIntValue(), pos -> new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<List<Entity>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = VillagerClusters.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lineBuilder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Vec3d forward = Vec3d.fromPolar((float)camera.getPitch(), (float)camera.getYaw()).multiply(2.0);
        int lR = ColorUtil.red(COLOR_LINE);
        int lG = ColorUtil.green(COLOR_LINE);
        int lB = ColorUtil.blue(COLOR_LINE);
        int bR = ColorUtil.red(COLOR_BOX);
        int bG = ColorUtil.green(COLOR_BOX);
        int bBl = ColorUtil.blue(COLOR_BOX);
        for (List<Entity> cluster : snapshot) {
            double cx = 0.0;
            double cy = 0.0;
            double cz = 0.0;
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxX = -1.7976931348623157E308;
            double maxY = -1.7976931348623157E308;
            double maxZ = -1.7976931348623157E308;
            for (Entity p : cluster) {
                cx += p.getX();
                cy += p.getY() + p.getHeight() / 2.0;
                cz += p.getZ();
                Box b = p.getBoundingBox();
                minX = Math.min(minX, b.minX);
                minY = Math.min(minY, b.minY);
                minZ = Math.min(minZ, b.minZ);
                maxX = Math.max(maxX, b.maxX);
                maxY = Math.max(maxY, b.maxY);
                maxZ = Math.max(maxZ, b.maxZ);
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
        if (!this.nullCheck()) {
            return;
        }
        List<List<Entity>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = VillagerClusters.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        for (List<Entity> cluster : snapshot) {
            double cx = 0.0;
            double cy = 0.0;
            double cz = 0.0;
            for (Entity p : cluster) {
                cx += p.getX();
                cy += p.getY() + p.getHeight() / 2.0;
                cz += p.getZ();
            }
            cx /= (double)cluster.size();
            cy /= (double)cluster.size();
            cz /= (double)cluster.size();
            int[] screen = this.worldToScreen(cx, cy, cz, camPos, camera);
            if (screen == null) continue;
            double dist = new Vec3d(VillagerClusters.mc.player.getX(), VillagerClusters.mc.player.getY(), VillagerClusters.mc.player.getZ()).distanceTo(new Vec3d(cx, cy, cz));
            String label = "\u00a7a\u229e " + cluster.size() + " villagers (" + String.format("%.1fm", dist) + ")";
            int textW = VillagerClusters.mc.textRenderer.getWidth(label);
            context.drawTextWithShadow(VillagerClusters.mc.textRenderer, label, screen[0] - textW / 2, screen[1] - 6, -16711936);
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
        double fovRad = Math.toRadians(((Integer)VillagerClusters.mc.options.getFov().getValue()).intValue());
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
