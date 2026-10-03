package com.nox.menu.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ProjectileTrajectory extends Module {
    public ProjectileTrajectory() {
        super("ProjectileTrajectory", "Predice la trayectoria de proyectiles | Predicts projectile trajectory", Category.RENDER);
    }

    @Override
    public void onWorldRender(PoseStack matrices, SubmitNodeCollector collector, float tickDelta) {
        if (!this.nullCheck() || !mc.player.isUsingItem()) return;

        ItemStack item = mc.player.getUseItem();
        boolean isBow = item.getItem() instanceof BowItem;
        boolean isCrossbow = item.getItem() instanceof CrossbowItem;
        boolean isTrident = item.getItem() instanceof TridentItem;

        if (!isBow && !isCrossbow && !isTrident) return;

        float velocity = 1.0f;
        if (isBow) {
            float drawTime = mc.player.getTicksUsingItem();
            velocity = drawTime / 20.0f;
            velocity = (velocity * velocity + velocity * 2.0f) / 3.0f;
            if (velocity > 1.0f) velocity = 1.0f;
        }

        if (velocity < 0.1f) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();

        double x = mc.player.getX() - Mth.cos(yaw / 180.0f * (float) Math.PI) * 0.16f;
        double y = mc.player.getY() + mc.player.getEyeHeight() - 0.1f;
        double z = mc.player.getZ() - Mth.sin(yaw / 180.0f * (float) Math.PI) * 0.16f;

        float f = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
        float g = -Mth.sin(pitch * 0.017453292F);
        float h = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);

        float power = isBow ? velocity * 3.0f : (isCrossbow ? 3.15f : 2.5f);

        Vec3 vel = new Vec3(f, g, h).normalize().scale(power);

        double gravity = isBow || isCrossbow || isTrident ? 0.05 : 0.03;
        double drag = 0.99;

        double[] x0 = {x}, y0 = {y}, z0 = {z};
        Vec3[] velRef = {vel};

        collector.submitCustomGeometry(matrices, RenderTypes.lines(), (pose, vc) -> {
            Matrix4f matrix = pose.pose();
            double curX = x0[0], curY = y0[0], curZ = z0[0];
            Vec3 curVel = velRef[0];

            double lastX = curX - camPos.x;
            double lastY = curY - camPos.y;
            double lastZ = curZ - camPos.z;

            for (int i = 0; i < 100; i++) {
                curX += curVel.x;
                curY += curVel.y;
                curZ += curVel.z;

                curVel = curVel.scale(drag);
                curVel = new Vec3(curVel.x, curVel.y - gravity, curVel.z);

                double relX = curX - camPos.x;
                double relY = curY - camPos.y;
                double relZ = curZ - camPos.z;

                vc.addVertex(matrix, (float) lastX, (float) lastY, (float) lastZ).setColor(255, 255, 255, 255).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);
                vc.addVertex(matrix, (float) relX, (float) relY, (float) relZ).setColor(255, 255, 255, 255).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(1.0f);

                lastX = relX;
                lastY = relY;
                lastZ = relZ;

                if (mc.level.getBlockState(new BlockPos((int) curX, (int) curY, (int) curZ)).isSolidRender()) {
                    break;
                }
            }
        });
    }
}
