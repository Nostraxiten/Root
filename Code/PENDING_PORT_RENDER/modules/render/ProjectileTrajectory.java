package com.nox.menu.modules.render;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ProjectileTrajectory extends Module {
    public ProjectileTrajectory() {
        super("ProjectileTrajectory", "Predice la trayectoria de proyectiles | Predicts projectile trajectory", Category.RENDER);
    }

    @Override
    public void onWorldRender(MatrixStack matrices, float tickDelta) {
        if (!this.nullCheck() || !mc.player.isUsingItem()) return;

        ItemStack item = mc.player.getActiveItem();
        boolean isBow = item.getItem() instanceof BowItem;
        boolean isCrossbow = item.getItem() instanceof CrossbowItem;
        boolean isTrident = item.getItem() instanceof TridentItem;

        if (!isBow && !isCrossbow && !isTrident) return;

        float velocity = 1.0f;
        if (isBow) {
            float drawTime = mc.player.getItemUseTime();
            velocity = drawTime / 20.0f;
            velocity = (velocity * velocity + velocity * 2.0f) / 3.0f;
            if (velocity > 1.0f) velocity = 1.0f;
        }

        if (velocity < 0.1f) return;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getCameraPos();

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        double x = mc.player.getX() - MathHelper.cos(yaw / 180.0f * (float)Math.PI) * 0.16f;
        double y = mc.player.getY() + mc.player.getStandingEyeHeight() - 0.1f;
        double z = mc.player.getZ() - MathHelper.sin(yaw / 180.0f * (float)Math.PI) * 0.16f;

        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float g = -MathHelper.sin(pitch * 0.017453292F);
        float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);

        float power = isBow ? velocity * 3.0f : (isCrossbow ? 3.15f : 2.5f);

        Vec3d vel = new Vec3d(f, g, h).normalize().multiply(power);

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = immediate.getBuffer(RenderLayers.LINES);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        double gravity = isBow || isCrossbow || isTrident ? 0.05 : 0.03;
        double drag = 0.99;

        double lastX = x - camPos.x;
        double lastY = y - camPos.y;
        double lastZ = z - camPos.z;

        for (int i = 0; i < 100; i++) {
            x += vel.x;
            y += vel.y;
            z += vel.z;

            vel = vel.multiply(drag);
            vel = new Vec3d(vel.x, vel.y - gravity, vel.z);

            double curX = x - camPos.x;
            double curY = y - camPos.y;
            double curZ = z - camPos.z;

            builder.vertex(matrix, (float)lastX, (float)lastY, (float)lastZ).color(255, 255, 255, 255).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);
            builder.vertex(matrix, (float)curX, (float)curY, (float)curZ).color(255, 255, 255, 255).normal(1.0f, 0.0f, 0.0f).lineWidth(1.0f);

            lastX = curX;
            lastY = curY;
            lastZ = curZ;

            if (mc.world.getBlockState(new net.minecraft.util.math.BlockPos((int)x, (int)y, (int)z)).isOpaque()) {
                break;
            }
        }
    }
}
