package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.gui.util.ColorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    // Per-type safety cap: if a single ore type matches this many blocks in one
    // scan (e.g. a dungeon deliberately built out of solid Ancient Debris/Gold Ore
    // walls), that type gets auto-disabled instead of being drawn — thousands of
    // boxes for one type is never useful, and drawing them all risks overflowing
    // the 16,777,215-vertex BufferBuilder limit (24 verts/box).
    private static final int PER_TYPE_CAP = 1500;

    private final OreGroup[] groups;
    private final Map<Block, OreGroup> blockToGroup = new HashMap<>();

    private static final class OreGroup {
        final BooleanSetting setting;
        final String label;
        final int color;
        OreGroup(BooleanSetting setting, String label, int color) {
            this.setting = setting;
            this.label = label;
            this.color = color;
        }
    }

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

        this.groups = new OreGroup[]{
                new OreGroup(this.diamond, "Diamond", -16711681),
                new OreGroup(this.ancient, "Ancient Debris", -48060),
                new OreGroup(this.gold, "Gold", -10496),
                new OreGroup(this.iron, "Iron", -3355444),
                new OreGroup(this.coal, "Coal", -10066330),
                new OreGroup(this.emerald, "Emerald", -16711868),
                new OreGroup(this.copper, "Copper", -35004),
        };
        this.mapBlock(Blocks.DIAMOND_ORE, this.groups[0]);
        this.mapBlock(Blocks.DEEPSLATE_DIAMOND_ORE, this.groups[0]);
        this.mapBlock(Blocks.ANCIENT_DEBRIS, this.groups[1]);
        this.mapBlock(Blocks.GOLD_ORE, this.groups[2]);
        this.mapBlock(Blocks.DEEPSLATE_GOLD_ORE, this.groups[2]);
        this.mapBlock(Blocks.NETHER_GOLD_ORE, this.groups[2]);
        this.mapBlock(Blocks.IRON_ORE, this.groups[3]);
        this.mapBlock(Blocks.DEEPSLATE_IRON_ORE, this.groups[3]);
        this.mapBlock(Blocks.COAL_ORE, this.groups[4]);
        this.mapBlock(Blocks.DEEPSLATE_COAL_ORE, this.groups[4]);
        this.mapBlock(Blocks.EMERALD_ORE, this.groups[5]);
        this.mapBlock(Blocks.DEEPSLATE_EMERALD_ORE, this.groups[5]);
        this.mapBlock(Blocks.COPPER_ORE, this.groups[6]);
        this.mapBlock(Blocks.DEEPSLATE_COPPER_ORE, this.groups[6]);
    }

    private void mapBlock(Block block, OreGroup group) {
        this.blockToGroup.put(block, group);
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

        Map<Block, OreGroup> activeTargets = new HashMap<>();
        for (Map.Entry<Block, OreGroup> e : this.blockToGroup.entrySet()) {
            if (e.getValue().setting.isEnabled()) {
                activeTargets.put(e.getKey(), e.getValue());
            }
        }
        if (activeTargets.isEmpty()) {
            this.foundOres = Collections.emptyList();
            return;
        }

        int playerChunkX = mc.player.chunkPosition().x();
        int playerChunkZ = mc.player.chunkPosition().z();
        int chunkRadius = (int) Math.ceil(this.radius.getValue() / 16.0) + 1;
        double radSq = this.radius.getValue() * this.radius.getValue();
        Vec3 playerPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        Map<OreGroup, Integer> groupCounts = new HashMap<>();
        List<OreGroup> tripped = new ArrayList<>();

        for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; ++cx) {
            for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; ++cz) {
                if (!mc.level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = mc.level.getChunk(cx, cz);
                if (chunk == null) continue;
                int worldMinY = mc.level.getMinY();
                int worldHeight = mc.level.getHeight();
                int yMin = Math.max(worldMinY, (int) (playerPos.y - this.radius.getValue()));
                int yMax = Math.min(worldMinY + worldHeight - 1, (int) (playerPos.y + this.radius.getValue()));
                for (int by = yMin; by <= yMax; ++by) {
                    for (int bx = cx * 16; bx < cx * 16 + 16; ++bx) {
                        for (int bz = cz * 16; bz < cz * 16 + 16; ++bz) {
                            double dx = (double) bx + 0.5 - playerPos.x;
                            double dy = (double) by + 0.5 - playerPos.y;
                            double dz = (double) bz + 0.5 - playerPos.z;
                            if (dx * dx + dy * dy + dz * dz > radSq) continue;
                            BlockPos pos = new BlockPos(bx, by, bz);
                            OreGroup group = activeTargets.get(chunk.getBlockState(pos).getBlock());
                            if (group == null) continue;
                            int count = groupCounts.getOrDefault(group, 0) + 1;
                            groupCounts.put(group, count);
                            if (count > PER_TYPE_CAP) {
                                if (!tripped.contains(group)) {
                                    tripped.add(group);
                                }
                                continue;
                            }
                            result.add(pos);
                        }
                    }
                }
            }
        }

        for (OreGroup group : tripped) {
            group.setting.setValue(false);
            if (mc.player != null) {
                mc.player.sendSystemMessage(Component.literal("§c[BlockESP] " + group.label
                        + " desactivado automaticamente: se detectaron mas de " + PER_TYPE_CAP
                        + " bloques de este tipo (riesgo de crash por vertices). Puedes reactivarlo manualmente."));
            }
        }

        this.foundOres = result;
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck()) {
            return;
        }
        List<BlockPos> snapshot = this.foundOres;
        if (snapshot.isEmpty()) {
            return;
        }
        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();
            for (BlockPos pos : snapshot) {
                OreGroup group = this.blockToGroup.get(mc.level.getBlockState(pos).getBlock());
                int color = group != null ? group.color : -1;
                int r = ColorUtil.red(color);
                int g = ColorUtil.green(color);
                int b = ColorUtil.blue(color);
                double drawX = (double) pos.getX() - camPos.x;
                double drawY = (double) pos.getY() - camPos.y;
                double drawZ = (double) pos.getZ() - camPos.z;
                AABB box = new AABB(drawX, drawY, drawZ, drawX + 1.0, drawY + 1.0, drawZ + 1.0).deflate(0.01);
                this.drawBoxLines(vc, matrix, box, r, g, b, 220);
            }
        });
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
