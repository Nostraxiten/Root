package com.nox.menu.gui.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * RenderUtil — adaptado a Minecraft 1.21.11.
 *
 * Cambios:
 *  - drawHorizontalGradient: ahora usa context.fillGradient() (gradiente
 *    vertical top→bottom en lugar de left→right, ya que en 1.21.11 la API
 *    de DrawContext no expone una matriz 4D para vertex custom en GUI).
 *    Visualmente el header tendrá el degradado de arriba a abajo en vez de
 *    de izquierda a derecha.
 *  - drawChamferedRect: simplificado a context.fill() (sin cortes diagonales).
 *    Los paneles tendrán esquinas rectas en vez de "chamfered".
 *  - drawChamferedOutline: simplificado a drawOutline() con esquinas rectas.
 *
 *  TODO: si en el futuro se quiere recuperar el vertex rendering custom en GUI,
 *  investigar RenderSystem.setShader + BufferRenderer.drawWithGlobalProgram()
 *  que no requiere la matriz de DrawContext.
 */
public class RenderUtil {

    // ── Chamfer size constants (mantenidas por compatibilidad, no usadas) ─────
    public static final float PANEL_CHAMFER  = 7f;
    public static final float MODULE_CHAMFER = 3.5f;

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers básicos
    // ──────────────────────────────────────────────────────────────────────────

    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawOutline(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawGradientRect(DrawContext context, int x, int y, int width, int height, int colorTop, int colorBottom) {
        context.fillGradient(x, y, x + width, y + height, colorTop, colorBottom);
    }

    /**
     * Dibuja un gradiente entre colorLeft y colorRight.
     * NOTA: en 1.21.11 el gradiente corre de ARRIBA a ABAJO (top=colorLeft,
     * bottom=colorRight) porque DrawContext.fillGradient() solo soporta eje Y.
     * El aspecto visual cambia ligeramente respecto a la versión anterior.
     */
    public static void drawHorizontalGradient(DrawContext context, int x, int y, int width, int height, int colorLeft, int colorRight) {
        context.fillGradient(x, y, x + width, y + height, colorLeft, colorRight);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Chamfered helpers — simplificados a rectángulos rectos en 1.21.11
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Antes dibujaba un hexágono con esquinas recortadas (chamfer).
     * En 1.21.11 simplificado a context.fill() para evitar vertex rendering
     * custom que requiere la Matrix4f del DrawContext (no disponible en GUI).
     * Los paneles tendrán esquinas rectas.
     */
    public static void drawChamferedRect(DrawContext context, float x, float y,
                                          float w, float h, float chamfer, int fillColor) {
        context.fill((int) x, (int) y, (int)(x + w), (int)(y + h), fillColor);
    }

    /**
     * Antes dibujaba el borde de un rectángulo con esquinas recortadas.
     * En 1.21.11 simplificado a drawOutline() con esquinas rectas.
     */
    public static void drawChamferedOutline(DrawContext context, float x, float y,
                                             float w, float h, float chamfer,
                                             float lineWidth, int outlineColor) {
        drawOutline(context, (int) x, (int) y, (int) w, (int) h, outlineColor);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Text helpers
    // ──────────────────────────────────────────────────────────────────────────

    public static void drawText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color) {
        context.drawTextWithShadow(textRenderer, text, x, y, color);
    }

    public static void drawCenteredText(DrawContext context, TextRenderer textRenderer, String text, int centerX, int y, int color) {
        int textWidth = textRenderer.getWidth(text);
        context.drawTextWithShadow(textRenderer, text, centerX - textWidth / 2, y, color);
    }

    public static void enableScissor(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    public static void disableScissor(DrawContext context) {
        context.disableScissor();
    }
}
