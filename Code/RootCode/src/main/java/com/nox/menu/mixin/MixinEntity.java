package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import com.nox.menu.modules.combat.HitboxExpand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class MixinEntity {

    @Inject(method={"getBoundingBox"}, at={@At(value="RETURN")}, cancellable=true)
    private void onGetBoundingAABB(CallbackInfoReturnable<AABB> cir) {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) {
            return;
        }
        Entity self = (Entity)(Object)this;
        if (self == Minecraft.getInstance().player) {
            return;
        }
        if (!(self instanceof LivingEntity)) {
            return;
        }
        HitboxExpand hitbox = manager.getModule(HitboxExpand.class);
        if (hitbox != null && hitbox.isEnabled()) {
            double expand = hitbox.getExpandAmount();
            cir.setReturnValue(((AABB)cir.getReturnValue()).inflate(expand, expand, expand));
        }
    }
}