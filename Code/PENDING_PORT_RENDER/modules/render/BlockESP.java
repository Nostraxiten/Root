/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.chunk.WorldChunk
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public class BlockESP
extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 32.0, 8.0, 64.0, 4.0);
    private final BooleanSetting diamond = new BooleanSetting("Diamond", true);
    private final BooleanSetting ancient = new BooleanSetting("Ancient", true);
    private final BooleanSetting gold = new BooleanSetting("Gold", true);
    private final BooleanSetting iron = new BooleanSetting("Iron", false);
    private final BooleanSetting coal = new BooleanSetting("Coal", false);
    private final BooleanSetting emerald = new BooleanSetting("Emerald", false);
    private final BooleanSetting copper = new BooleanSetting("Copper", false);
    private volatile List<BlockPos> foundOres = Collections.emptyList();
    private int tickCounter = 0;
    private static final int SCAN_INTERVAL = 20;

    public BlockESP() {
        super("BlockESP", "Resalta ores valiosos a traves de las paredes. | Highlights valuable ores through walls.", Category.RENDER);
        this.addSetting(this.radius);
        this.addSetting(this.diamond);
        this.addSetting(this.ancient);
        this.addSetting(this.gold);
        this.addSetting(this.iron);
        this.addSetting(this.coal);
        this.addSetting(this.emerald);
        this.addSetting(this.copper);
    }

    @Override
    public void onEnable() {
        this.foundOres = Collections.emptyList();
        this.tickCounter = 0;
    }

    @Override
    public void onDisable() {
        this.foundOres = Collections.emptyList();
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) {
            return;
        }
        if (++this.tickCounter < 20) {
            return;
        }
        this.tickCounter = 0;
        Set<Block> targets = this.buildTargetSet();
        if (targets.isEmpty()) {
            this.foundOres = Collections.emptyList();
            return;
        }
        int playerChunkX = BlockESP.mc.player.getChunkPos().x;
        int playerChunkZ = BlockESP.mc.player.getChunkPos().z;
        int chunkRadius = (int)Math.ceil((Double)this.radius.getValue() / 16.0) + 1;
        double radSq = (Double)this.radius.getValue() * (Double)this.radius.getValue();
        Vec3d playerPos = new Vec3d(BlockESP.mc.player.getX(), BlockESP.mc.player.getY(), BlockESP.mc.player.getZ());
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; ++cx) {
            for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; ++cz) {
                WorldChunk chunk;
                if (!BlockESP.mc.world.isChunkLoaded(cx, cz) || (chunk = BlockESP.mc.world.getChunk(cx, cz)) == null) continue;
                int worldMinY = BlockESP.mc.world.getBottomY();
                int worldHeight = BlockESP.mc.world.getHeight();
                int yMin = Math.max(worldMinY, (int)(playerPos.y - (Double)this.radius.getValue()));
                int yMax = Math.min(worldMinY + worldHeight - 1, (int)(playerPos.y + (Double)this.radius.getValue()));
                for (int by = yMin; by <= yMax; ++by) {
                    for (int bx = cx * 16; bx < cx * 16 + 16; ++bx) {
                        for (int bz = cz * 16; bz < cz * 16 + 16; ++bz) {
                            BlockPos pos;
                            Block block;
                            double dx = (double)bx + 0.5 - playerPos.x;
                            double dy = (double)by + 0.5 - playerPos.y;
                            double dz = (double)bz + 0.5 - playerPos.z;
                            if (dx * dx + dy * dy + dz * dz > radSq || !targets.contains(block = chunk.getBlockState(pos = new BlockPos(bx, by, bz)).getBlock())) continue;
                            result.add(pos);
                        }
                    }
                }
            }
        }
        this.foundOres = result;
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<BlockPos> snapshot = this.foundOres;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = BlockESP.mc.gameRenderer.getCamera();
        Vec3d camPos = new Vec3d(camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lineBuilder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        for (BlockPos pos : snapshot) {
            int color = this.oreColor(BlockESP.mc.world.getBlockState(pos).getBlock());
            int r = ColorUtil.red(color);
            int g = ColorUtil.green(color);
            int b = ColorUtil.blue(color);
            double drawX = (double)pos.getX() - camPos.x;
            double drawY = (double)pos.getY() - camPos.y;
            double drawZ = (double)pos.getZ() - camPos.z;
            Box box = new Box(drawX, drawY, drawZ, drawX + 1.0, drawY + 1.0, drawZ + 1.0).expand(-0.01);
            this.drawBoxLines(lineBuilder, matrix, box, r, g, b, 220);
        }
    }

    private Set<Block> buildTargetSet() {
        HashSet<Block> s = new HashSet<Block>();
        if (((Boolean)this.diamond.getValue()).booleanValue()) {
            s.add(Blocks.DIAMOND_ORE);
            s.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        }
        if (((Boolean)this.ancient.getValue()).booleanValue()) {
            s.add(Blocks.ANCIENT_DEBRIS);
        }
        if (((Boolean)this.gold.getValue()).booleanValue()) {
            s.add(Blocks.GOLD_ORE);
            s.add(Blocks.DEEPSLATE_GOLD_ORE);
            s.add(Blocks.NETHER_GOLD_ORE);
        }
        if (((Boolean)this.iron.getValue()).booleanValue()) {
            s.add(Blocks.IRON_ORE);
            s.add(Blocks.DEEPSLATE_IRON_ORE);
        }
        if (((Boolean)this.coal.getValue()).booleanValue()) {
            s.add(Blocks.COAL_ORE);
            s.add(Blocks.DEEPSLATE_COAL_ORE);
        }
        if (((Boolean)this.emerald.getValue()).booleanValue()) {
            s.add(Blocks.EMERALD_ORE);
            s.add(Blocks.DEEPSLATE_EMERALD_ORE);
        }
        if (((Boolean)this.copper.getValue()).booleanValue()) {
            s.add(Blocks.COPPER_ORE);
            s.add(Blocks.DEEPSLATE_COPPER_ORE);
        }
        return s;
    }

    private int oreColor(Block block) {
        if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            return -16711681;
        }
        if (block == Blocks.ANCIENT_DEBRIS) {
            return -48060;
        }
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
            return -10496;
        }
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            return -3355444;
        }
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            return -10066330;
        }
        if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            return -16711868;
        }
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            return -35004;
        }
        return -1;
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

