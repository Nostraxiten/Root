package com.nox.menu.gui.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

/**
 * RenderUtil — adaptado a Minecraft 26.2.
 *
 * 26.2 reemplazó DrawContext por GuiGraphicsExtractor, pero conserva el mismo
 * sistema de "render state" diferido (GuiRenderState / GuiElementRenderState)
 * que 1.21.11. GuiGraphicsExtractor.fillGradient() sólo interpola en el eje Y
 * (arriba→abajo), igual que antes, así que no sirve para gradientes
 * horizontales.
 *
 * drawHorizontalGradient encola su propio elemento (HorizontalGradientRenderState)
 * en el mismo GuiRenderState que usa GuiGraphicsExtractor.fill()/fillGradient(),
 * respetando el scissor y el orden de dibujado igual que el resto de la GUI,
 * pero interpolando el color en el eje X.
 */
public class RenderUtil {

    public static final float PANEL_CHAMFER  = 7f;
    public static final float MODULE_CHAMFER = 3.5f;

    public static void drawRect(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawOutline(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawGradientRect(GuiGraphicsExtractor context, int x, int y, int width, int height, int colorTop, int colorBottom) {
        context.fillGradient(x, y, x + width, y + height, colorTop, colorBottom);
    }

    /** Gradiente izquierda→derecha real (colorLeft en x, colorRight en x+width). */
    public static void drawHorizontalGradient(GuiGraphicsExtractor context, int x, int y, int width, int height, int colorLeft, int colorRight) {
        Matrix3x2f pose = new Matrix3x2f(context.pose());
        ScreenRectangle scissor = context.scissorStack.peek();
        context.guiRenderState.addGuiElement(new HorizontalGradientRenderState(
                RenderPipelines.GUI, TextureSetup.noTexture(), pose,
                x, y, x + width, y + height, colorLeft, colorRight, scissor
        ));
    }

    private record HorizontalGradientRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2fc pose,
            int x0, int y0, int x1, int y1,
            int colorLeft, int colorRight,
            ScreenRectangle scissorArea,
            ScreenRectangle bounds
    ) implements GuiElementRenderState {

        HorizontalGradientRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose,
                                       int x0, int y0, int x1, int y1, int colorLeft, int colorRight,
                                       ScreenRectangle scissorArea) {
            this(pipeline, textureSetup, pose, x0, y0, x1, y1, colorLeft, colorRight, scissorArea,
                    computeBounds(x0, y0, x1, y1, pose, scissorArea));
        }

        @Override
        public void buildVertices(VertexConsumer vertices) {
            vertices.addVertexWith2DPose(this.pose, this.x0, this.y0).setColor(this.colorLeft);
            vertices.addVertexWith2DPose(this.pose, this.x0, this.y1).setColor(this.colorLeft);
            vertices.addVertexWith2DPose(this.pose, this.x1, this.y1).setColor(this.colorRight);
            vertices.addVertexWith2DPose(this.pose, this.x1, this.y0).setColor(this.colorRight);
        }

        private static ScreenRectangle computeBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, ScreenRectangle scissorArea) {
            ScreenRectangle rect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
            return scissorArea != null ? scissorArea.intersection(rect) : rect;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Chamfered helpers — simplificados a rectángulos rectos en 26.2
    // (no relacionado con el bug de color; esquinas rectas en vez de chamfer)
    // ──────────────────────────────────────────────────────────────────────────

    public static void drawChamferedRect(GuiGraphicsExtractor context, float x, float y,
                                          float w, float h, float chamfer, int fillColor) {
        context.fill((int) x, (int) y, (int)(x + w), (int)(y + h), fillColor);
    }

    public static void drawChamferedOutline(GuiGraphicsExtractor context, float x, float y,
                                             float w, float h, float chamfer,
                                             float lineWidth, int outlineColor) {
        drawOutline(context, (int) x, (int) y, (int) w, (int) h, outlineColor);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Text helpers
    // ──────────────────────────────────────────────────────────────────────────

    public static void drawText(GuiGraphicsExtractor context, Font font, String text, int x, int y, int color) {
        context.text(font, text, x, y, color, true);
    }

    public static void drawCenteredText(GuiGraphicsExtractor context, Font font, String text, int centerX, int y, int color) {
        int textWidth = font.width(text);
        context.text(font, text, centerX - textWidth / 2, y, color, true);
    }

    public static void enableScissor(GuiGraphicsExtractor context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    public static void disableScissor(GuiGraphicsExtractor context) {
        context.disableScissor();
    }
}
