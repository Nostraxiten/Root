package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class StorageESP
extends Module {
    private final BooleanSetting chests = new BooleanSetting("Chests", true);
    private final BooleanSetting barrels = new BooleanSetting("Barrels", true);
    private final BooleanSetting shulkers = new BooleanSetting("Shulkers", true);

    private static final int SCAN_INTERVAL = 20;

    // Per-type safety cap: if a single storage type matches this many block
    // entities in one scan (a storage-dense area, and the scan radius can be as
    // large as the server's render distance), that type gets auto-disabled
    // instead of being drawn — thousands of boxes for one type risks overflowing
    // the 16,777,215-vertex BufferBuilder limit (24 verts/box).
    private static final int PER_TYPE_CAP = 1500;

    private int tickCounter = 0;
    private volatile List<Found> found = Collections.emptyList();

    private static final class Found {
        final BlockPos pos;
        final int color;
        Found(BlockPos pos, int color) {
            this.pos = pos;
            this.color = color;
        }
    }

    public StorageESP() {
        super("StorageESP", "Muestra cofres, barriles y almacenamiento a traves de las paredes. | Shows chests, barrels and storage through walls.", Category.RENDER);
        this.addSetting(this.chests);
        this.addSetting(this.barrels);
        this.addSetting(this.shulkers);
    }

    @Override
    public void onEnable() {
        this.found = Collections.emptyList();
        this.tickCounter = 0;
    }

    @Override
    public void onDisable() {
        this.found = Collections.emptyList();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (++this.tickCounter < SCAN_INTERVAL) {
            return;
        }
        this.tickCounter = 0;

        boolean wantChests = this.chests.isEnabled();
        boolean wantBarrels = this.barrels.isEnabled();
        boolean wantShulkers = this.shulkers.isEnabled();
        if (!wantChests && !wantBarrels && !wantShulkers) {
            this.found = Collections.emptyList();
            return;
        }

        int chunkX = mc.player.chunkPosition().x();
        int chunkZ = mc.player.chunkPosition().z();
        int radius = mc.options.getEffectiveRenderDistance();
        ArrayList<Found> result = new ArrayList<Found>();
        int chestCount = 0, barrelCount = 0, shulkerCount = 0;
        boolean chestsTripped = false, barrelsTripped = false, shulkersTripped = false;

        for (int x = chunkX - radius; x <= chunkX + radius; ++x) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; ++z) {
                if (!mc.level.hasChunk(x, z)) continue;
                LevelChunk chunk = mc.level.getChunk(x, z);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    int color;
                    if (be instanceof ChestBlockEntity) {
                        if (!wantChests) continue;
                        if (++chestCount > PER_TYPE_CAP) { chestsTripped = true; continue; }
                        color = -10496;
                    } else if (be instanceof BarrelBlockEntity) {
                        if (!wantBarrels) continue;
                        if (++barrelCount > PER_TYPE_CAP) { barrelsTripped = true; continue; }
                        color = -3308225;
                    } else if (be instanceof ShulkerBoxBlockEntity) {
                        if (!wantShulkers) continue;
                        if (++shulkerCount > PER_TYPE_CAP) { shulkersTripped = true; continue; }
                        color = -65281;
                    } else {
                        continue;
                    }
                    result.add(new Found(be.getBlockPos(), color));
                }
            }
        }

        if (chestsTripped) {
            this.chests.setValue(false);
            this.warn("Chests");
        }
        if (barrelsTripped) {
            this.barrels.setValue(false);
            this.warn("Barrels");
        }
        if (shulkersTripped) {
            this.shulkers.setValue(false);
            this.warn("Shulkers");
        }

        this.found = result;
    }

    private void warn(String label) {
        if (mc.player == null) {
            return;
        }
        mc.player.sendSystemMessage(Component.literal("§c[StorageESP] " + label
                + " desactivado automaticamente: se detectaron mas de " + PER_TYPE_CAP
                + " bloques de este tipo (riesgo de crash por vertices). Puedes reactivarlo manualmente."));
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<Found> snapshot = this.found;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();
            for (Found f : snapshot) {
                int r = ColorUtil.red(f.color);
                int g = ColorUtil.green(f.color);
                int b = ColorUtil.blue(f.color);
                double drawX = (double) f.pos.getX() - camPos.x;
                double drawY = (double) f.pos.getY() - camPos.y;
                double drawZ = (double) f.pos.getZ() - camPos.z;
                AABB box = new AABB(drawX, drawY, drawZ, drawX + 1.0, drawY + 1.0, drawZ + 1.0).deflate(0.02);
                this.drawBoxLines(vc, matrix, box, r, g, b, 255);
            }
        });
    }

    private void drawBoxLines(VertexConsumer builder, Matrix4f matrix, AABB b, int red, int green, int blue, int alpha) {
        float minX = (float) b.minX;
        float minY = (float) b.minY;
        float minZ = (float) b.minZ;
        float maxX = (float) b.maxX;
        float maxY = (float) b.maxY;
        float maxZ = (float) b.maxZ;
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(1.0f);
    }
}
