/*
 * NoxMenu 2.0.0 - XRay Module (Optimized)
 * Targets are rebuilt ONLY on enable or when a setting changes.
 * No per-tick polling. No redundant chunk reloads.
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class XRay
extends Module {
    private final BooleanSetting ores = new BooleanSetting("Ores", true);
    private final BooleanSetting storage = new BooleanSetting("Storage", true);
    private final BooleanSetting fluids = new BooleanSetting("Fluids", true);
    private final BooleanSetting spawners = new BooleanSetting("Spawners", true);
    private final BooleanSetting autoBright = new BooleanSetting("FullBright", true);

    private static volatile Set<Block> TARGET_BLOCKS = Collections.emptySet();
    private double savedGamma = 1.0;
    private static volatile boolean ACTIVE = false;
    private static XRay ACTIVE_INSTANCE = null;

    private boolean prevOres;
    private boolean prevStorage;
    private boolean prevFluids;
    private boolean prevSpawners;

    public XRay() {
        super("XRay", "Revela minerales, cofres y spawners | Reveals ores, chests and spawners", Category.RENDER);
        this.addSetting(this.ores);
        this.addSetting(this.storage);
        this.addSetting(this.fluids);
        this.addSetting(this.spawners);
        this.addSetting(this.autoBright);
        this.prevOres = this.ores.isEnabled();
        this.prevStorage = this.storage.isEnabled();
        this.prevFluids = this.fluids.isEnabled();
        this.prevSpawners = this.spawners.isEnabled();
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) {
            return;
        }
        rebuildTargetSet();
        ACTIVE_INSTANCE = this;
        ACTIVE = true;
        snapshotSettings();
        if (this.autoBright.isEnabled()) {
            this.savedGamma = XRay.mc.options.gamma().get();
            XRay.mc.options.gamma().set(1.0);
        }
        reloadChunks();
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) {
            return;
        }
        ACTIVE = false;
        ACTIVE_INSTANCE = null;
        TARGET_BLOCKS = Collections.emptySet();
        if (this.autoBright.isEnabled()) {
            XRay.mc.options.gamma().set(this.savedGamma);
        }
        reloadChunks();
    }

    @Override
    public void onTick() {
        if (!this.isEnabled()) {
            return;
        }
        boolean curOres = this.ores.isEnabled();
        boolean curStorage = this.storage.isEnabled();
        boolean curFluids = this.fluids.isEnabled();
        boolean curSpawners = this.spawners.isEnabled();

        if (curOres != prevOres || curStorage != prevStorage
            || curFluids != prevFluids || curSpawners != prevSpawners) {
            snapshotSettings();
            rebuildTargetSet();
            if (this.nullCheck()) {
                reloadChunks();
            }
        }
    }

    private void snapshotSettings() {
        this.prevOres = this.ores.isEnabled();
        this.prevStorage = this.storage.isEnabled();
        this.prevFluids = this.fluids.isEnabled();
        this.prevSpawners = this.spawners.isEnabled();
    }

    private void reloadChunks() {
        if (XRay.mc.level == null || XRay.mc.player == null) {
            return;
        }
        int cx = XRay.mc.player.chunkPosition().x();
        int cz = XRay.mc.player.chunkPosition().z();
        int r = XRay.mc.options.getEffectiveRenderDistance() + 1;
        int minSY = XRay.mc.level.getMinSectionY();
        int maxSY = XRay.mc.level.getMaxSectionY();
        XRay.mc.level.setSectionRangeDirty(cx - r, minSY, cz - r, cx + r, maxSY, cz + r);
    }

    public static boolean isXRayActive() {
        return ACTIVE;
    }

    public static boolean shouldRenderBlock(Block block) {
        return ACTIVE && TARGET_BLOCKS.contains(block);
    }

    private void rebuildTargetSet() {
        Set<Block> set = new HashSet<>();
        if (this.ores.isEnabled()) {
            addOres(set);
        }
        if (this.storage.isEnabled()) {
            addStorage(set);
        }
        if (this.fluids.isEnabled()) {
            addFluids(set);
        }
        if (this.spawners.isEnabled()) {
            addSpawners(set);
        }
        TARGET_BLOCKS = Collections.unmodifiableSet(set);
    }

    private static void addOres(Set<Block> set) {
        set.add(Blocks.COAL_ORE);
        set.add(Blocks.DEEPSLATE_COAL_ORE);
        set.add(Blocks.IRON_ORE);
        set.add(Blocks.DEEPSLATE_IRON_ORE);
        set.add(Blocks.GOLD_ORE);
        set.add(Blocks.DEEPSLATE_GOLD_ORE);
        set.add(Blocks.DIAMOND_ORE);
        set.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        set.add(Blocks.EMERALD_ORE);
        set.add(Blocks.DEEPSLATE_EMERALD_ORE);
        set.add(Blocks.LAPIS_ORE);
        set.add(Blocks.DEEPSLATE_LAPIS_ORE);
        set.add(Blocks.REDSTONE_ORE);
        set.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        set.add(Blocks.COPPER_ORE);
        set.add(Blocks.DEEPSLATE_COPPER_ORE);
        set.add(Blocks.NETHER_GOLD_ORE);
        set.add(Blocks.NETHER_QUARTZ_ORE);
        set.add(Blocks.ANCIENT_DEBRIS);
    }

    private static void addStorage(Set<Block> set) {
        set.add(Blocks.CHEST);
        set.add(Blocks.TRAPPED_CHEST);
        set.add(Blocks.BARREL);
        set.add(Blocks.ENDER_CHEST);
        set.add(Blocks.FURNACE);
        set.add(Blocks.BLAST_FURNACE);
        set.add(Blocks.SMOKER);
        set.add(Blocks.DISPENSER);
        set.add(Blocks.DROPPER);
        set.add(Blocks.HOPPER);
        set.add(Blocks.SHULKER_BOX);
        set.addAll(Blocks.DYED_SHULKER_BOX.asList());
    }

    private static void addFluids(Set<Block> set) {
        set.add(Blocks.WATER);
        set.add(Blocks.LAVA);
    }

    private static void addSpawners(Set<Block> set) {
        set.add(Blocks.SPAWNER);
    }
}
