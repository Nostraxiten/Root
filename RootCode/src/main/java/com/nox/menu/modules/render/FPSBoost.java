/*
 * NoxMenu 6.0.0 - FPSBoost Module (v3.0)
 * Client-side optimizations to improve frame rate.
 *
 * Confirmados con mixin real:
 * - NoWeather        -> MixinWorldRenderer#renderWeather
 * - NoClouds         -> MixinWorldRenderer#renderClouds
 * - AnimationThrottle/AnimSkipTicks -> MixinSpriteContents#tick
 * - StaticAnim       -> MixinSpriteContents#tick (congela animación al 100%)
 * - NoMenuBlur       -> MixinGameRenderer#renderBlur
 * - LowFire          -> MixinInGameOverlayRenderer#renderFireOverlay
 *                       (InGameOverlayRenderer, Yarn 1.21.5+build.1)
 * - StaticDrops      -> MixinItemEntityRenderer#redirectMultiply
 *                       (ItemEntityRenderer, Yarn 1.21.5+build.1)
 * - LimitEntities    -> MixinEntityRenderDispatcher#shouldRender
 *                       (EntityRenderDispatcher, Yarn 1.21.5+build.1)
 * - FastGlint        -> MixinItemRendererGlint#getItemGlintConsumer +
 *                       getArmorGlintConsumer (ItemRenderer, vanilla puro;
 *                       si se añade Sodium habrá que revisar este hook)
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;

public class FPSBoost extends Module {
    private final BooleanSetting noWeather = new BooleanSetting("NoWeather", true);
    private final BooleanSetting noClouds = new BooleanSetting("NoClouds", true);
    private final BooleanSetting noMenuBlur = new BooleanSetting("NoMenuBlur", true);

    private static volatile boolean ACTIVE = false;
    private static volatile FPSBoost ACTIVE_INSTANCE = null;

    public FPSBoost() {
        super("FPSBoost", "Mejora el rendimiento desactivando elementos pesados de renderizado | Improves FPS by disabling heavy visual effects", Category.OPTIMIZE);
        this.addSetting(this.noWeather);
        this.addSetting(this.noClouds);
        this.addSetting(this.noMenuBlur);
        this.setEnabled(true);
    }

    @Override
    public void onEnable() {
        ACTIVE = true;
        ACTIVE_INSTANCE = this;
    }

    @Override
    public void onDisable() {
        ACTIVE = false;
        ACTIVE_INSTANCE = null;
    }

    public static boolean isActive() {
        return ACTIVE;
    }

    public static boolean shouldDisableWeather() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.noWeather.isEnabled();
    }

    public static boolean shouldDisableClouds() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.noClouds.isEnabled();
    }

    public static boolean shouldDisableMenuBlur() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.noMenuBlur.isEnabled();
    }
}