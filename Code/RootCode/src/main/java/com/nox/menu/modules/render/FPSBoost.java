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
import com.nox.menu.core.setting.NumberSetting;

public class FPSBoost
extends Module {
    private final BooleanSetting noWeather = new BooleanSetting("NoWeather", true);
    private final BooleanSetting noClouds = new BooleanSetting("NoClouds", true);
    private final BooleanSetting noMenuBlur = new BooleanSetting("NoMenuBlur", true);
    private final BooleanSetting animationThrottle = new BooleanSetting("AnimationThrottle", true);
    private final NumberSetting animationSkipTicks = new NumberSetting("AnimSkipTicks", 2.0, 0.0, 3.0, 1.0);
    /** Congela la animación de texturas completamente (100% estática). */
    private final BooleanSetting staticAnim = new BooleanSetting("StaticAnim", false);

    // --- Nuevas opciones (v2.3) ---
    private final BooleanSetting lowFire = new BooleanSetting("LowFire", false);
    /** Escala del overlay de fuego (0.1 = muy pequeño, 1.0 = tamaño vanilla). */
    private final NumberSetting fireScale = new NumberSetting("FireScale", 0.4, 0.1, 1.0, 0.1);
    private final BooleanSetting staticDrops = new BooleanSetting("StaticDrops", false);
    private final BooleanSetting limitEntities = new BooleanSetting("LimitEntities", false);
    /** Distancia máxima de render de entidades en bloques. */
    private final NumberSetting entityRenderDist = new NumberSetting("EntityDist", 32.0, 8.0, 128.0, 4.0);
    private final BooleanSetting fastGlint = new BooleanSetting("FastGlint", false);

    private static volatile boolean ACTIVE = false;
    private static volatile FPSBoost ACTIVE_INSTANCE = null;

    // Contador de ticks para AnimationThrottle. Solo lo toca el hilo de render
    // (se avanza desde MixinSpriteContents), así que no necesita ser atómico.
    private static int animationTickCounter = 0;

    public FPSBoost() {
        super("FPSBoost", "Mejora el rendimiento ajustando el renderizado. | Improves performance by tweaking rendering.", Category.OPTIMIZE);
        this.addSetting(this.noWeather);
        this.addSetting(this.noClouds);
        this.addSetting(this.noMenuBlur);
        this.addSetting(this.animationThrottle);
        this.addSetting(this.animationSkipTicks);
        this.addSetting(this.staticAnim);
        this.addSetting(this.lowFire);
        this.addSetting(this.fireScale);
        this.addSetting(this.staticDrops);
        this.addSetting(this.limitEntities);
        this.addSetting(this.entityRenderDist);
        this.addSetting(this.fastGlint);
        this.setEnabled(true);
    }

    @Override
    public void onEnable() {
        ACTIVE = true;
        ACTIVE_INSTANCE = this;
        animationTickCounter = 0;
    }

    @Override
    public void onDisable() {
        ACTIVE = false;
        ACTIVE_INSTANCE = null;
    }

    // --- Static accessors for mixins ---

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

    /**
     * Llamar una vez por tick de animación de textura (MixinSpriteContents).
     * Devuelve true si ESTE tick debe SALTARSE (cancelarse).
     *
     * - StaticAnim ON  → siempre cancela → animación 100% congelada.
     * - AnimationThrottle ON + AnimSkipTicks N → cancela N de cada N+1 ticks.
     */
    public static boolean shouldSkipAnimationTick() {
        FPSBoost m = ACTIVE_INSTANCE;
        if (!ACTIVE || m == null) return false;

        // StaticAnim: congelar completamente, cancelar siempre.
        if (m.staticAnim.isEnabled()) return true;

        if (!m.animationThrottle.isEnabled()) return false;

        int skip = m.animationSkipTicks.getIntValue();
        if (skip <= 0) return false;

        animationTickCounter++;
        if (animationTickCounter > skip) {
            animationTickCounter = 0;
            return false;
        }
        return true;
    }

    // --- Accessors nuevos (v2.3) ---

    /** @return true si LowFire está activo (reducir overlay de fuego). */
    public static boolean shouldLowFire() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.lowFire.isEnabled();
    }

    /**
     * @return escala del overlay de fuego (0.1–1.0).
     * Solo llamar cuando shouldLowFire() == true.
     */
    public static float getFireScale() {
        FPSBoost m = ACTIVE_INSTANCE;
        if (m == null) return 1.0f;
        return m.fireScale.getFloatValue();
    }

    /** @return true si StaticDrops está activo (congelar rotación de drops). */
    public static boolean shouldStaticDrops() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.staticDrops.isEnabled();
    }

    /** @return true si LimitEntities/EntityDist está activo. */
    public static boolean shouldLimitEntityDist() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.limitEntities.isEnabled();
    }

    /**
     * @return distancia máxima de render de entidades en bloques.
     * Solo llamar cuando shouldLimitEntityDist() == true.
     */
    public static double getEntityRenderDist() {
        FPSBoost m = ACTIVE_INSTANCE;
        if (m == null) return 32.0;
        return m.entityRenderDist.getValue();
    }

    /** @return true si FastGlint está activo (una sola pasada de glint). */
    public static boolean shouldFastGlint() {
        FPSBoost m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.fastGlint.isEnabled();
    }
}