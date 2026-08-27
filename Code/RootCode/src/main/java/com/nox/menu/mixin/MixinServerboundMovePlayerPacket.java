/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package com.nox.menu.mixin;

import com.nox.menu.core.IServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ServerboundMovePlayerPacket.class})
public class MixinServerboundMovePlayerPacket
implements IServerboundMovePlayerPacket {
    @Mutable
    @Shadow
    private boolean onGround;

    @Override
    public void noxMenu$setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}

