package com.nox.menu.core;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.config.SettingsIO;
import com.nox.menu.modules.combat.AntiKnockback;
import com.nox.menu.modules.combat.AutoBlock;
import com.nox.menu.modules.combat.AutoTotem;
import com.nox.menu.modules.combat.HitboxExpand;
import com.nox.menu.modules.combat.KillAura;
import com.nox.menu.modules.combat.CrystalAura;
import com.nox.menu.modules.combat.Surround;
import com.nox.menu.modules.combat.TriggerBot;
import com.nox.menu.modules.movement.AntiVoid;
import com.nox.menu.modules.movement.AutoSprint;
import com.nox.menu.modules.movement.Fly;
import com.nox.menu.modules.movement.NoclipTP;
import com.nox.menu.modules.movement.Jesus;
import com.nox.menu.modules.movement.NoFall;
import com.nox.menu.modules.movement.Speed;
import com.nox.menu.modules.movement.Spider;
import com.nox.menu.modules.movement.Step;
import com.nox.menu.modules.movement.OrbitCam;
import com.nox.menu.modules.player.AntiAFK;
import com.nox.menu.modules.player.AutoEat;
import com.nox.menu.modules.player.AutoArmor;
import com.nox.menu.modules.player.AutoFish;
import com.nox.menu.modules.player.AutoRespawn;
import com.nox.menu.modules.player.ChestStealer;
import com.nox.menu.modules.player.PanicKey;
import com.nox.menu.modules.render.CustomOutline;
import com.nox.menu.modules.render.Freecam;
import com.nox.menu.modules.render.NoFog;
import com.nox.menu.modules.render.NoParticles;
import com.nox.menu.modules.render.Zoom;
import com.nox.menu.modules.render.FPSBoost;
import com.nox.menu.modules.render.ChunkOptimizer;
import com.nox.menu.modules.combat.AutoDodge;
import com.nox.menu.modules.player.AutoClicker;
import com.nox.menu.modules.player.XPBOT;
import com.nox.menu.modules.theme.CustomTheme;
import com.nox.menu.modules.world.AutoFarm;
import com.nox.menu.modules.world.AutoMine;
import com.nox.menu.modules.world.AutoTool;
import com.nox.menu.modules.world.Scaffold;
import com.nox.menu.modules.world.FastPlace;
import com.nox.menu.modules.world.Waypoints;
import com.nox.menu.modules.world.NightVision;
import com.nox.menu.modules.world.VillagerClusters;
import com.nox.menu.modules.world.Chunks;
import com.nox.menu.modules.movement.Sit;
import com.nox.menu.modules.player.DeathCoords;
import com.nox.menu.modules.player.HUDOverlay;
import com.nox.menu.modules.player.NotificationSystem;
import com.nox.menu.modules.render.ESP;
import com.nox.menu.modules.render.Tracers;
import com.nox.menu.modules.render.BlockESP;
import com.nox.menu.modules.render.StorageESP;
import com.nox.menu.modules.render.ChestClusters;
import com.nox.menu.modules.render.ProjectileTrajectory;
import com.nox.menu.modules.render.XRay;
import com.nox.menu.modules.optimize.LeavesOptimizer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// NOTE (26.2 port, phase 1): the render-dependent modules (ESP, Tracers, BlockESP,
// StorageESP, ChestClusters, VillagerClusters, Chunks, ProjectileTrajectory, XRay,
// LeavesOptimizer, Sit, DeathCoords, HUDOverlay, NotificationSystem) and the ClickGUI
// itself live in PENDING_PORT_RENDER/ next to RootCode until 26.2's new
// SubmitNodeCollector render pipeline is ported (phase 2). Everything registered below
// is logic-only and does not touch rendering.
public class ModuleManager {
    private static ModuleManager INSTANCE;
    private final List<Module> modules = new ArrayList<Module>();
    private boolean isInitializing = true;

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    public ModuleManager() {
        INSTANCE = this;
    }

    public void init() {
        this.register(new KillAura());
        this.register(new CrystalAura());
        this.register(new Surround());
        this.register(new AutoBlock());
        this.register(new TriggerBot());
        this.register(new AntiKnockback());
        this.register(new AutoTotem());
        this.register(new HitboxExpand());
        this.register(new AutoDodge());
        this.register(new Speed());
        this.register(new Fly());
        this.register(new NoFall());
        this.register(new Spider());
        this.register(new Jesus());
        this.register(new Step());
        this.register(new AntiVoid());
        this.register(new AutoSprint());
        this.register(new NoclipTP());
        this.register(new OrbitCam());
        this.register(new Sit());
        this.register(new Freecam());
        this.register(new NoFog());
        this.register(new NoParticles());
        this.register(new Zoom());
        this.register(new ESP());
        this.register(new Tracers());
        this.register(new BlockESP());
        this.register(new StorageESP());
        this.register(new ChestClusters());
        this.register(new ProjectileTrajectory());
        this.register(new XRay());
        this.register(new AutoMine());
        this.register(new Scaffold());
        this.register(new FastPlace());
        this.register(new AutoFish());
        this.register(new AutoTool());
        this.register(new AutoFarm());
        this.register(new VillagerClusters());
        this.register(new Chunks());
        this.register(new AutoRespawn());
        this.register(new AutoEat());
        this.register(new AutoArmor());
        this.register(new AntiAFK());
        this.register(new ChestStealer());
        this.register(new XPBOT());
        this.register(new PanicKey());
        this.register(new AutoClicker());
        this.register(new DeathCoords());
        this.register(new HUDOverlay());
        this.register(new NotificationSystem());
        this.register(new CustomTheme());
        this.register(new CustomOutline());
        this.register(new LeavesOptimizer());
        this.register(new FPSBoost());
        this.register(new ChunkOptimizer());
        // this.register(new Waypoints());
        this.register(new NightVision());
        SettingsIO.load(this);
        this.isInitializing = false;
    }

    private void register(Module module) {
        this.modules.add(module);
    }

    public void onTick() {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onTick();
        }
    }

    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onWorldRender(matrices, collector, tickDelta);
        }
    }

    public void onHudRender(GuiGraphicsExtractor context, float tickDelta) {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onHudRender(context, tickDelta);
        }
    }

    public void onKeyPress(int keyCode) {
        for (Module module : this.modules) {
            if (module.getKeyBind() != keyCode) continue;
            module.toggle();
        }
    }

    public void onModuleToggled(Module module) {
        this.save();
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return this.modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public Module getModule(String name) {
        return this.modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T)((Module)this.modules.stream().filter(m -> m.getClass() == clazz).findFirst().orElse(null));
    }

    public void disableAll() {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.setEnabled(false);
        }
    }

    public void save() {
        if (this.isInitializing) return;
        SettingsIO.save(this);
    }
}
