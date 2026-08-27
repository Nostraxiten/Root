package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {
    private final BooleanSetting placeBelow = new BooleanSetting("Place Below", false);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", false);

    private final BlockPos[] SURROUND_OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    public Surround() {
        super("Surround", "Rodea al jugador con bloques de proteccion | Surrounds player with protection blocks", Category.COMBAT);
        this.addSetting(this.placeBelow);
        this.addSetting(this.rotate);
    }

    @Override
    public void onTick() {
        if (!this.nullCheck()) return;

        BlockPos playerPos = mc.player.blockPosition();
        List<BlockPos> positionsToPlace = new ArrayList<>();

        if (this.placeBelow.isEnabled()) {
            positionsToPlace.add(playerPos.below());
        }

        for (BlockPos offset : SURROUND_OFFSETS) {
            positionsToPlace.add(playerPos.offset(offset));
        }

        int slot = findValidBlockSlot();
        if (slot == -1) return;

        int oldSlot = mc.player.getInventory().getSelectedSlot();

        for (BlockPos pos : positionsToPlace) {
            if (mc.level.getBlockState(pos).canBeReplaced()) {
                if (mc.player.getBoundingBox().intersects(new net.minecraft.world.phys.AABB(pos))) {
                    continue;
                }
                BlockHitResult hitResult = getValidPlacement(pos);
                if (hitResult != null) {
                    placeBlock(hitResult, slot, oldSlot);
                    return; // Solo 1 bloque por tick
                }
            }
        }
    }

    private void placeBlock(BlockHitResult hitResult, int newSlot, int oldSlot) {
        if (newSlot != oldSlot) {
            mc.player.getInventory().setSelectedSlot(newSlot);
            mc.player.connection.send(new ServerboundSetCarriedItemPacket(newSlot));
        }

        if (this.rotate.isEnabled()) {
            float[] rotations = getRotations(hitResult.getLocation());
            mc.player.connection.send(new ServerboundMovePlayerPacket.Rot(rotations[0], rotations[1], mc.player.onGround(), false));
        }

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);
        mc.player.swing(InteractionHand.MAIN_HAND);

        if (newSlot != oldSlot) {
            mc.player.getInventory().setSelectedSlot(oldSlot);
            mc.player.connection.send(new ServerboundSetCarriedItemPacket(oldSlot));
        }
    }

    private BlockHitResult getValidPlacement(BlockPos targetPos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = targetPos.relative(direction);
            BlockState state = mc.level.getBlockState(neighbor);

            if (!state.canBeReplaced()) {
                Direction opposite = direction.getOpposite();
                Vec3 hitVec = new Vec3(neighbor.getX() + 0.5, neighbor.getY() + 0.5, neighbor.getZ() + 0.5)
                        .add(new Vec3(opposite.getStepX(), opposite.getStepY(), opposite.getStepZ()).scale(0.5));
                return new BlockHitResult(hitVec, direction.getOpposite(), neighbor, false);
            }
        }
        return null;
    }

    private int findValidBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                BlockItem blockItem = (BlockItem) stack.getItem();
                if (!(blockItem.getBlock() instanceof FallingBlock) && blockItem.getBlock().defaultBlockState().isSolidRender()) {
                    return i;
                }
            }
        }
        return -1;
    }

    private float[] getRotations(Vec3 target) {
        Vec3 eyesPos = new Vec3(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        double diffX = target.x - eyesPos.x;
        double diffY = target.y - eyesPos.y;
        double diffZ = target.z - eyesPos.z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));
        return new float[]{yaw, pitch};
    }
}
