/*
 * NoxMenu 2.0.0 - ChunkOptimizer Module (v2)
 * Reduces chunk loading stutter by throttling the number of chunk section
 * rebuilds that the renderer processes per frame, and by smoothing out
 * the chunk update schedule.
 *
 * v2 - fixes y mejoras:
 *
 * 1) FIX condición de carrera: shouldThrottleRebuild() se invoca "antes de
 *    cada rebuild de sección de chunk", pero en Minecraft el chunk mesh
 *    building (ChunkBuilder) corre en un POOL de hilos de fondo, no en el
 *    hilo de render. El int/long planos de v1 (rebuildsThisFrame++,
 *    lastRebuildTime = ...) no son thread-safe: varios hilos worker pueden
 *    leer el mismo valor antes de que ninguno lo actualice, así que el
 *    throttle podía tanto perder incrementos (el límite nunca se alcanza de
 *    verdad) como dejar pasar ráfagas simultáneas (justo lo que se quería
 *    evitar). Ahora se usa AtomicInteger con incrementAndGet() para reservar
 *    el "slot" de forma atómica, y AtomicLong + compareAndSet() para el
 *    delay, de forma que solo un hilo gana la carrera por actualizar el
 *    timestamp en cada instante.
 *
 * 2) NUEVO - Adaptive Throttle: en vez de un MaxRebuilds fijo, mide el frame
 *    time real entre onFrameStart() y ajusta el límite efectivo hacia arriba
 *    o abajo para acercarse a un Target FPS configurable. En hardware potente
 *    esto deja pasar más rebuilds (menos popping de chunks) sin coste real de
 *    FPS; en hardware limitado, aprieta más el throttle cuando detecta que ya
 *    vas por debajo del objetivo, en vez de seguir forzando el mismo tope fijo.
 */
package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.NumberSetting;
import com.nox.menu.core.setting.BooleanSetting;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChunkOptimizer
extends Module {
    private final NumberSetting maxRebuildsPerFrame = new NumberSetting("MaxRebuilds", 16.0, 1.0, 32.0, 1.0);
    private final NumberSetting rebuildDelayMs = new NumberSetting("RebuildDelay", 50.0, 0.0, 100.0, 5.0);
    private final BooleanSetting lazyChunks = new BooleanSetting("LazyChunks", false);

    // NUEVO: throttling adaptativo por FPS objetivo
    private final BooleanSetting adaptiveMode = new BooleanSetting("AdaptiveMode", true);
    private final NumberSetting targetFps = new NumberSetting("TargetFPS", 60.0, 30.0, 240.0, 5.0);
    private final NumberSetting minRebuildsPerFrame = new NumberSetting("MinRebuilds", 2.0, 1.0, 16.0, 1.0);

    private static volatile boolean ACTIVE = false;
    private static ChunkOptimizer ACTIVE_INSTANCE = null;

    // FIX: contadores atómicos — shouldThrottleRebuild() se llama desde los
    // hilos worker del ChunkBuilder, no desde el hilo de render, así que un
    // int/long plano sin sincronizar era una condición de carrera real.
    private static final AtomicInteger rebuildsThisFrame = new AtomicInteger(0);
    private static final AtomicLong lastRebuildTime = new AtomicLong(0);

    // Tracking de frame time para el modo adaptativo (esto sí se toca solo
    // desde onFrameStart(), que corre en el hilo de render, así que no
    // necesita ser atómico).
    private static long lastFrameStartNanos = 0L;
    private static double smoothedFrameTimeMs = 16.6; // arranca asumiendo 60fps
    private static volatile int effectiveMaxRebuilds = 16;

    public ChunkOptimizer() {
        super("ChunkOptimizer", "Reduce el stuttering limitando rebuilds de chunks. | Reduces stuttering by limiting chunk rebuilds.", Category.OPTIMIZE);
        this.addSetting(this.maxRebuildsPerFrame);
        this.addSetting(this.rebuildDelayMs);
        this.addSetting(this.lazyChunks);
        this.addSetting(this.adaptiveMode);
        this.addSetting(this.targetFps);
        this.addSetting(this.minRebuildsPerFrame);
        this.setEnabled(true);
    }

    @Override
    public void onEnable() {
        ACTIVE = true;
        ACTIVE_INSTANCE = this;
        lastFrameStartNanos = 0L;
        smoothedFrameTimeMs = 16.6;
        effectiveMaxRebuilds = (int) maxRebuildsPerFrame.getIntValue();
    }

    @Override
    public void onDisable() {
        ACTIVE = false;
        ACTIVE_INSTANCE = null;
    }

    // --- Static accessors for mixin ---

    public static boolean isActive() {
        return ACTIVE;
    }

    /**
     * Called at the start of each frame (hilo de render) to resetear el
     * contador por-frame y, si Adaptive Mode está activo, recalcular cuántos
     * rebuilds se permiten este frame según el frame time real medido.
     */
    public static void onFrameStart() {
        ChunkOptimizer m = ACTIVE_INSTANCE;

        long now = System.nanoTime();
        if (lastFrameStartNanos != 0L) {
            double instantFrameMs = (now - lastFrameStartNanos) / 1_000_000.0;
            // Media móvil simple para no reaccionar a un único frame suelto
            // (picos puntuales de otras causas no deberían afectar al throttle).
            smoothedFrameTimeMs = smoothedFrameTimeMs * 0.9 + instantFrameMs * 0.1;
        }
        lastFrameStartNanos = now;

        if (m != null && m.adaptiveMode.isEnabled()) {
            recalcAdaptiveLimit(m);
        } else if (m != null) {
            effectiveMaxRebuilds = m.maxRebuildsPerFrame.getIntValue();
        }

        rebuildsThisFrame.set(0);
    }

    /**
     * Ajusta effectiveMaxRebuilds hacia el configurado (maxRebuildsPerFrame)
     * si vamos sobrados de frame time respecto al objetivo, o lo aprieta
     * hacia minRebuildsPerFrame si ya estamos por debajo del FPS objetivo.
     * Cambios graduales (±1 por frame) para que no se note un salto brusco
     * en el throttle de un frame a otro.
     */
    private static void recalcAdaptiveLimit(ChunkOptimizer m) {
        double targetFrameMs = 1000.0 / m.targetFps.getValue();
        int configuredMax = m.maxRebuildsPerFrame.getIntValue();
        int configuredMin = m.minRebuildsPerFrame.getIntValue();

        if (smoothedFrameTimeMs > targetFrameMs * 1.1) {
            // Vamos por debajo del FPS objetivo: aprieta el throttle.
            effectiveMaxRebuilds = Math.max(configuredMin, effectiveMaxRebuilds - 1);
        } else if (smoothedFrameTimeMs < targetFrameMs * 0.85) {
            // Margen de sobra: deja pasar más rebuilds para reducir popping.
            effectiveMaxRebuilds = Math.min(configuredMax, effectiveMaxRebuilds + 1);
        }
        // Entre 0.85x y 1.1x del frame time objetivo: zona muerta, no tocar
        // nada para evitar oscilar de un lado a otro cada frame.
    }

    /**
     * Called before each chunk section rebuild — OJO: esto se invoca desde
     * los hilos worker del ChunkBuilder, potencialmente varios a la vez.
     * Returns true if the rebuild should be SKIPPED (throttled).
     */
    public static boolean shouldThrottleRebuild() {
        if (!ACTIVE) return false;
        ChunkOptimizer m = ACTIVE_INSTANCE;
        if (m == null) return false;

        if (mc.gui.screen() != null) {
            return false; // No throttle while any screen (terrain loading, etc.) is shown
        }

        int maxRebuilds = m.adaptiveMode.isEnabled() ? effectiveMaxRebuilds : m.maxRebuildsPerFrame.getIntValue();

        // FIX: reserva atómica del slot. incrementAndGet() es la operación
        // que decide "gano yo esta ronda" — si nos pasamos del límite,
        // deshacemos el incremento y throttleamos. Esto es lo que evita la
        // condición de carrera de v1 (rebuildsThisFrame++ sin atomicidad).
        int current = rebuildsThisFrame.incrementAndGet();
        if (current > maxRebuilds) {
            rebuildsThisFrame.decrementAndGet();
            return true;
        }

        double delayMs = (Double) m.rebuildDelayMs.getValue();
        if (delayMs > 0) {
            long now = System.nanoTime();
            long previous = lastRebuildTime.get();
            long elapsedMs = (now - previous) / 1_000_000;
            if (elapsedMs < delayMs) {
                rebuildsThisFrame.decrementAndGet();
                return true;
            }
            // CAS: si otro hilo ya actualizó lastRebuildTime en el instante
            // entre el get() y aquí, simplemente no pisamos su timestamp más
            // reciente (él ganó la carrera del delay); nuestro rebuild ya
            // tiene su slot de frame reservado arriba, así que se permite igual.
            lastRebuildTime.compareAndSet(previous, now);
        }

        return false;
    }

    public static boolean isLazyChunks() {
        ChunkOptimizer m = ACTIVE_INSTANCE;
        return ACTIVE && m != null && m.lazyChunks.isEnabled();
    }
}