package com.nox.menu.modules.world;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;

/**
 * NightVision — Permanent fullbright-style night vision.
 *
 * Implementation: does NOT apply the real Night Vision potion effect.
 * Instead, MixinLightmapTextureManager intercepts the NightVisionFactor
 * local variable (slot 9) inside LightmapTextureManager.update(float)
 * and forces it to the configured strength value.
 * This means:
 *  - No potion icon on HUD
 *  - No duration limit
 *  - No server-side detectable status effect
 *  - Instant on/off (lightmap updates every frame)
 *  - Compatible with real night vision potion (both produce 1.0f, no conflict)
 *  - Works in Overworld, Nether, End, underwater, in caves
 */
public class NightVision extends Module {

    /**
     * Strength of the fake night vision factor injected into the lightmap.
     * 1.0 = full brightness like night vision. 0.0 = disabled (vanilla).
     * Values between add partial night-vision for a more subtle look.
     */
    public final NumberSetting strength = new NumberSetting("Strength", 1.0, 0.1, 1.0, 0.05);

    public NightVision() {
        super("NightVision",
              "Visión nocturna permanente sin efecto de poción. Manipula el lightmap directamente. | Permanent night vision without potion effect. Directly manipulates the lightmap.",
              Category.WORLD);
        this.addSetting(this.strength);
    }

    /** Called by MixinLightmapTextureManager to get the configured strength. */
    public float getStrength() {
        return strength.getFloatValue();
    }
}
