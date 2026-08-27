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
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.world.chunk.WorldChunk
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
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class StorageESP
extends Module {
    private final BooleanSetting chests = new BooleanSetting("Chests", true);
    private final BooleanSetting barrels = new BooleanSetting("Barrels", true);
    private final BooleanSetting shulkers = new BooleanSetting("Shulkers", true);

    public StorageESP() {
        super("StorageESP", "Muestra cofres, barriles y almacenamiento a traves de las paredes. | Shows chests, barrels and storage through walls.", Category.RENDER);
        this.addSetting(this.chests);
        this.addSetting(this.barrels);
        this.addSetting(this.shulkers);
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        Camera camera = StorageESP.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        ArrayList<BlockEntity> storageBlocks = new ArrayList<BlockEntity>();
        int chunkX = StorageESP.mc.player.getChunkPos().x;
        int chunkZ = StorageESP.mc.player.getChunkPos().z;
        int radius = (Integer)StorageESP.mc.options.getViewDistance().getValue();
        for (int x = chunkX - radius; x <= chunkX + radius; ++x) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; ++z) {
                WorldChunk chunk;
                if (!StorageESP.mc.world.isChunkLoaded(x, z) || (chunk = StorageESP.mc.world.getChunk(x, z)) == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof ChestBlockEntity && (Boolean)this.chests.getValue() != false || be instanceof BarrelBlockEntity && (Boolean)this.barrels.getValue() != false) && (!(be instanceof ShulkerBoxBlockEntity) || !((Boolean)this.shulkers.getValue()).booleanValue())) continue;
                    storageBlocks.add(be);
                }
            }
        }
        for (BlockEntity be : storageBlocks) {
            BlockPos pos = be.getPos();
            int color = 0;
            if (be instanceof ChestBlockEntity) {
                color = -10496;
            } else if (be instanceof BarrelBlockEntity) {
                color = -3308225;
            } else if (be instanceof ShulkerBoxBlockEntity) {
                color = -65281;
            }
            int r = ColorUtil.red(color);
            int g = ColorUtil.green(color);
            int b = ColorUtil.blue(color);
            double drawX = (double)pos.getX() - camPos.x;
            double drawY = (double)pos.getY() - camPos.y;
            double drawZ = (double)pos.getZ() - camPos.z;
            Box box = new Box(drawX, drawY, drawZ, drawX + 1.0, drawY + 1.0, drawZ + 1.0);
            box = box.expand(-0.02);
            this.drawBoxLines(builder, matrix, box, r, g, b, 255);
        }
    }

    private void drawBoxLines(VertexConsumer builder, Matrix4f matrix, Box b, int red, int green, int blue, int alpha) {
        float minX = (float)b.minX;
        float minY = (float)b.minY;
        float minZ = (float)b.minZ;
        float maxX = (float)b.maxX;
        float maxY = (float)b.maxY;
        float maxZ = (float)b.maxZ;
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, 1.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).normal(-1.0f, 0.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).normal(0.0f, 0.0f, -1.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).normal(0.0f, 1.0f, 0.0f).lineWidth(1.0f);
    }
}

