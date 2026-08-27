package com.nox.menu.mixin;

import com.nox.menu.core.ModuleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class MixinLivingEntity {

    @Inject(method="getAttributeValue", at=@At("RETURN"), cancellable=true)
    private void onGetAttributeValue(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute == Attributes.ENTITY_INTERACTION_RANGE || attribute == Attributes.BLOCK_INTERACTION_RANGE) {
            ModuleManager manager = ModuleManager.getInstance();
            if (manager != null) {
                if (false) {
                }
            }
        }
    }

    @Inject(method={"getSpeed()F"}, at={@At(value="RETURN")}, cancellable=true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self != Minecraft.getInstance().player) {
            return;
        }
        if (false && self.isUsingItem()) {
            float speed = ((Float)cir.getReturnValue()).floatValue();
            cir.setReturnValue(Float.valueOf(speed / 0.2f));
        }
    }
}