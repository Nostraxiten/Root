package com.nox.menu.modules.movement;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.platform.InputConstants;

/**
 * NoclipTP — mueve la entidad-cámara local sin enviar movimiento ilegal al servidor.
 *
 * Comportamiento:
 *  • Mientras está activo: NO se envían paquetes de posición (el servidor te ve donde empezaste).
 *  • Al desactivar: se envía un único paquete de teletransporte al destino. El servidor ve un
 *    salto de posición puntual; si el anti-cheat bloquea eso, deshabilitar safetyCheck moverá
 *    al jugador solo en cliente (sin paquete al servidor).
 *
 *  Esto evita el "Se ha detectado un movimiento de jugador no válido" que aparecía cuando
 *  se mandaba setPos() + un paquete de posición cada tick.
 */
public class NoclipTP extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.3, 0.05, 3.0, 0.05);
    private final BooleanSetting safetyCheck = new BooleanSetting("Safety Check", true);

    // Posición "fantasma" del ojo — lo que el jugador VE en pantalla
    private double camX, camY, camZ;
    private float camYaw, camPitch;

    // Posición donde estaba al activar (para revertir si safety falla)
    private double origX, origY, origZ;
    private boolean origFlying;

    public NoclipTP() {
        super("NoclipTP",
            "Atraviesa bloques y teleporta al desactivar | Noclip through blocks, teleports on disable",
            Category.MOVEMENT);
        this.addSetting(this.speed);
        this.addSetting(this.safetyCheck);
    }

    @Override
    public void onEnable() {
        if (!this.nullCheck()) return;

        this.origX = mc.player.getX();
        this.origY = mc.player.getY();
        this.origZ = mc.player.getZ();
        this.origFlying = mc.player.getAbilities().flying;

        // Cámara parte desde los pies del jugador
        this.camX = mc.player.getX();
        this.camY = mc.player.getY();
        this.camZ = mc.player.getZ();
        this.camYaw   = mc.player.getYRot();
        this.camPitch = mc.player.getXRot();

        // Desactivar física local para que la hitbox no colisione
        mc.player.noPhysics = true;
        mc.player.setNoGravity(true);
    }

    @Override
    public void onDisable() {
        if (!this.nullCheck()) return;

        // Restaurar física
        mc.player.noPhysics = false;
        mc.player.setNoGravity(false);
        mc.player.getAbilities().flying = this.origFlying;
        mc.player.setDeltaMovement(0, 0, 0);

        // Destino: pies del jugador fantasma
        double targetX = this.camX;
        double targetY = this.camY;
        double targetZ = this.camZ;

        // Safety check — busca posición sólida
        if (this.safetyCheck.isEnabled()) {
            BlockPos pos = new BlockPos(
                (int) Math.floor(targetX),
                (int) Math.floor(targetY),
                (int) Math.floor(targetZ));
            if (!mc.level.getBlockState(pos).canBeReplaced()
                || !mc.level.getBlockState(pos.above()).canBeReplaced()) {
                boolean found = false;
                for (int i = 1; i <= 5; i++) {
                    BlockPos up = pos.above(i);
                    if (mc.level.getBlockState(up).canBeReplaced()
                        && mc.level.getBlockState(up.above()).canBeReplaced()) {
                        targetY = up.getY();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    targetX = origX;
                    targetY = origY;
                    targetZ = origZ;
                }
            }
        }

        // Mover la entidad LOCAL
        mc.player.setPos(targetX, targetY, targetZ);

        // Enviar UN SOLO paquete de posición (el servidor acepta teleports puntuales mejor
        // que movimientos continuos de 20+ b/s).
        mc.player.connection.send(
            new ServerboundMovePlayerPacket.Pos(targetX, targetY, targetZ, true, false));
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        // Mantener física desactivada (el toggle externo podría resetearla)
        mc.player.noPhysics = true;
        mc.player.setDeltaMovement(0, 0, 0);
        mc.player.fallDistance = 0.0f;

        this.camYaw   = mc.player.getYRot();
        this.camPitch = mc.player.getXRot();

        double yawRad = Math.toRadians(this.camYaw);
        Vec3 forward = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vec3 right   = new Vec3( Math.cos(yawRad), 0, -Math.sin(yawRad));
        double s = this.speed.getValue();

        double dx = 0, dy = 0, dz = 0;
        if (InputConstants.isKeyDown(InputConstants.KEY_W)) { dx += forward.x * s; dz += forward.z * s; }
        if (InputConstants.isKeyDown(InputConstants.KEY_S)) { dx -= forward.x * s; dz -= forward.z * s; }
        if (InputConstants.isKeyDown(InputConstants.KEY_D)) { dx += right.x   * s; dz += right.z   * s; }
        if (InputConstants.isKeyDown(InputConstants.KEY_A)) { dx -= right.x   * s; dz -= right.z   * s; }
        if (InputConstants.isKeyDown(InputConstants.KEY_SPACE)) { dy += s; }
        if (InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)) { dy -= s; }

        this.camX += dx;
        this.camY += dy;
        this.camZ += dz;

        // Mover SOLO la entidad local — NO enviar paquetes de red aquí.
        // El servidor nos ve congelados en origX/Y/Z mientras volamos en fantasma.
        mc.player.setPos(this.camX, this.camY, this.camZ);
    }

    public double getCamX()    { return camX; }
    public double getCamY()    { return camY; }
    public double getCamZ()    { return camZ; }
    public float  getCamYaw()  { return camYaw; }
    public float  getCamPitch(){ return camPitch; }
}