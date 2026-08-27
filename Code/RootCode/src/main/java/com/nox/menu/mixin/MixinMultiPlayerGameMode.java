/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.MultiPlayerGameMode
 *  org.spongepowered.asm.mixin.Mixin
 */
package com.nox.menu.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.nox.menu.core.ModuleManager;

@Mixin(value={MultiPlayerGameMode.class})
public abstract class MixinMultiPlayerGameMode {

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//         Object hammer = null;
        if (false) {
//             hammer.onBlockBreak(pos);
        }
    }
}

