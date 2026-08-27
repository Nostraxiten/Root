package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class NoclipTP extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1);
    private final BooleanSetting safetyCheck = new BooleanSetting("Safety Check", true);

    // Posición "de cámara" (ojo del jugador), igual que antes
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    private double originalX;
    private double originalY;
    private double originalZ;
    private boolean originalFlying;

    // Contador para no spamear paquetes de red cada frame
    private int netTickCounter = 0;

    public NoclipTP() {
        super("NoclipTP", "Atraviesa bloques y teleporta al desactivar | Noclip through blocks, teleports on disable", Category.MOVEMENT);
        this.addSetting(this.speed);
        this.addSetting(this.safetyCheck);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) return;

        this.originalX = mc.player.getX();
        this.originalY = mc.player.getY();
        this.originalZ = mc.player.getZ();
        this.originalFlying = mc.player.getAbilities().flying;

        this.x = mc.player.getX();
        this.y = mc.player.getY() + mc.player.getEyeHeight();
        this.z = mc.player.getZ();
        this.yaw = mc.player.getYRot();
        this.pitch = mc.player.getXRot();

        mc.player.noPhysics = true;
        // Evita que la física vanilla (gravedad, colisión) siga tocando al jugador
        mc.player.setNoGravity(true);
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) return;

        mc.player.noPhysics = false;
        mc.player.setNoGravity(false);
        mc.player.getAbilities().flying = this.originalFlying;
        mc.player.setDeltaMovement(0, 0, 0);

        double targetX = this.x;
        double targetY = this.y - mc.player.getEyeHeight();
        double targetZ = this.z;

        if (this.safetyCheck.isEnabled()) {
            BlockPos pos = new BlockPos((int) Math.floor(targetX), (int) Math.floor(targetY), (int) Math.floor(targetZ));
            if (!mc.level.getBlockState(pos).canBeReplaced() || !mc.level.getBlockState(pos.above()).canBeReplaced()) {
                boolean safe = false;
                for (int i = 0; i < 3; i++) {
                    pos = pos.above();
                    if (mc.level.getBlockState(pos).canBeReplaced() && mc.level.getBlockState(pos.above()).canBeReplaced()) {
                        targetY = pos.getY();
                        safe = true;
                        break;
                    }
                }
                if (!safe) {
                    targetX = originalX;
                    targetY = originalY;
                    targetZ = originalZ;
                }
            }
        }

        com.nox.menu.modules.movement.NoFall nofall = com.nox.menu.core.ModuleManager.getInstance().getModule(com.nox.menu.modules.movement.NoFall.class);
        boolean originalNofall = false;
        if (nofall != null) {
            originalNofall = nofall.isEnabled();
            if (!originalNofall) {
                nofall.setEnabled(true);
            }
        }

        mc.player.setPos(targetX, targetY, targetZ);
        mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(targetX, targetY, targetZ, true, false));

        if (nofall != null && !originalNofall) {
            new Thread(() -> {
                try {
                    Thread.sleep(50);
                    mc.execute(() -> nofall.setEnabled(false));
                } catch (Exception e) {}
            }).start();
        }
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        mc.player.noPhysics = true;

        this.yaw = mc.player.getYRot();
        this.pitch = mc.player.getXRot();

        double yawRad = Math.toRadians(this.yaw);
        Vec3 forward = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vec3 right = new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
        double s = this.speed.getValue();

        long window = mc.getWindow().handle();

        double dx = 0, dy = 0, dz = 0;

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1) {
            dx += forward.x * s;
            dz += forward.z * s;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == 1) {
            dx -= forward.x * s;
            dz -= forward.z * s;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == 1) {
            dx += right.x * s;
            dz += right.z * s;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == 1) {
            dx -= right.x * s;
            dz -= right.z * s;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == 1) {
            dy += s;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == 1) {
            dy -= s;
        }

        this.x += dx;
        this.y += dy;
        this.z += dz;

        // CLAVE: mover la entidad REAL cada tick, no solo la variable interna.
        // Esto es lo que faltaba: sin esto, la hitbox nunca se desplazaba y
        // cualquier "paso a través de paredes" dependía de sistemas externos
        // (p. ej. anti-clip de cámara) que no tratan igual los 3 ejes.
        double realY = this.y - mc.player.getEyeHeight();
        mc.player.setPos(this.x, realY, this.z);

        // Anulamos velocidad DESPUÉS de mover, para que no se acumule
        // gravedad/impulso residual entre ticks.
        mc.player.setDeltaMovement(0.0, 0.0, 0.0);
        mc.player.fallDistance = 0.0f;

        // Enviamos el paquete de posición al servidor cada tick (o cada 1-2 ticks
        // si te preocupa el volumen de paquetes) para que el servidor no te
        // "tire para atrás" por desincronización de colisión.
        netTickCounter++;
        if (netTickCounter % 1 == 0) {
            mc.player.connection.send(
                new ServerboundMovePlayerPacket.Pos(this.x, realY, this.z, false, false)
            );
        }
    }

    public double getCamX() { return this.x; }
    public double getCamY() { return this.y; }
    public double getCamZ() { return this.z; }
    public float getCamYaw() { return this.yaw; }
    public float getCamPitch() { return this.pitch; }
}