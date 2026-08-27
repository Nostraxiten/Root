package com.nox.menu.modules.world;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
        double radSq = this.scanRadius.getValue() * this.scanRadius.getValue();
        Vec3 playerPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        ArrayList<Entity> found = new ArrayList<Entity>();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Villager || (this.includeZombieVillagers.isEnabled() && entity instanceof ZombieVillager)) {
                if (entity.distanceToSqr(playerPos) <= radSq) {
                    found.add(entity);
                }
            }
        }
        this.clusters = ClusterUtils.clusterByProximity(found, this.clusterDistance.getValue(), this.minClusterSize.getIntValue(),
                pos -> new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<List<Entity>> snapshot = this.clusters;
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
            for (List<Entity> cluster : snapshot) {
                double cx = 0.0, cy = 0.0, cz = 0.0;
                double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
                double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
                for (Entity p : cluster) {
                    cx += p.getX();
                    cy += p.getY() + p.getBbHeight() / 2.0;
                    cz += p.getZ();
                    AABB b = p.getBoundingBox();
                    minX = Math.min(minX, b.minX);
                    minY = Math.min(minY, b.minY);
                    minZ = Math.min(minZ, b.minZ);
                    maxX = Math.max(maxX, b.maxX);
                    maxY = Math.max(maxY, b.maxY);
                    maxZ = Math.max(maxZ, b.maxZ);
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
        if (!this.nullCheck()) {
            return;
        }
        List<List<Entity>> snapshot = this.clusters;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();
        for (List<Entity> cluster : snapshot) {
            double cx = 0.0, cy = 0.0, cz = 0.0;
            for (Entity p : cluster) {
                cx += p.getX();
                cy += p.getY() + p.getBbHeight() / 2.0;
                cz += p.getZ();
            }
            cx /= cluster.size();
            cy /= cluster.size();
            cz /= cluster.size();
            int[] screen = this.worldToScreen(cx, cy, cz, camPos, camera);
            if (screen == null) continue;
            double dist = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()).distanceTo(new Vec3(cx, cy, cz));
            String label = "§a⊞ " + cluster.size() + " villagers (" + String.format("%.1fm", dist) + ")";
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
