/*
 * NoxMenu 2.0.0 - LocalPlayer Mixin
 * Handles: AutoSprint, Step, Spider, NoSlowdown (item/shield use), Object, Object
 */
package com.nox.menu.mixin;

import com.nox.menu.modules.movement.AutoSprint;
import com.nox.menu.modules.movement.Spider;
import com.nox.menu.modules.movement.Step;
import com.nox.menu.core.ModuleManager;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LocalPlayer.class})
public abstract class MixinLocalPlayer {
    private float noxMenu$originalYaw;
    private float noxMenu$originalPitch;

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void onTickTail(CallbackInfo ci) {
        Spider spider;
        Step step;
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        LocalPlayer player = (LocalPlayer)(Object)this;
        AutoSprint autoSprint = manager.getModule(AutoSprint.class);
        if (autoSprint != null && autoSprint.isEnabled() && (player.input.keyPresses.forward() || player.input.keyPresses.backward() || player.input.keyPresses.left() || player.input.keyPresses.right())) {
            player.setSprinting(true);
        }
        if ((step = manager.getModule(Step.class)) != null && step.isEnabled()) {
            step.applyStepHeight(player);
        }
        if ((spider = manager.getModule(Spider.class)) != null && spider.isEnabled()) {
            spider.applyClimb(player);
        }
    }

    @Inject(method={"sendPosition"}, at={@At(value="HEAD")})
    private void onSendMovementPacketsHead(CallbackInfo ci) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;
        LocalPlayer player = (LocalPlayer)(Object)this;
        this.noxMenu$originalYaw = player.getYRot();
        this.noxMenu$originalPitch = player.getXRot();
    }

    @Inject(method={"sendPosition"}, at={@At(value="TAIL")})
    private void onSendMovementPacketsTail(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer)(Object)this;
        player.setYRot(this.noxMenu$originalYaw);
        player.setXRot(this.noxMenu$originalPitch);
    }

    /**
     * NoSlowdown: Redirect isUsingItem() in tickMovement() to prevent the
     * item-use slowdown (eating, drinking, bow, shield blocking).
     * When NoSlowdown blocks item slowdown, we tell the movement code
     * the player is NOT using an item, so no slowdown is applied.
     */
    @Redirect(method={"modifyInput"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"), require=0)
    private boolean onIsUsingItemTickMovement(LocalPlayer player) {
        if (false) {
            return false;
        }
        return player.isUsingItem();
    }

    /**
     * NoSlowdown: In 1.21+, input slowdown happens in tick() instead of tickMovement().
     * We need to intercept the isUsingItem() call inside tick() so the input multiplier is 1.0f.
     */
    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"), require=0)
    private boolean onIsUsingItemTick(LocalPlayer player) {
        if (false) {
            return false;
        }
        return player.isUsingItem();
    }

    /**
     * NoSlowdown: Also intercept the slowMovement call in tickMovement().
     * In 1.21.5, slowMovement() is called from entity collision (cobwebs etc.).
     * This redirect ensures we override the slow multiplier.
     */
    @Redirect(method={"aiStep"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V", ordinal=0), require=0)
    private void onSetSprinting(LocalPlayer player, boolean sprinting) {
        if (false && !sprinting && player.isUsingItem()) {
            return;
        }
        player.setSprinting(sprinting);
    }
}
