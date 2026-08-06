package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityVisibilityMixin {
    @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true)
    private void modifyVisibility(Entity lookingEntity, CallbackInfoReturnable<Double> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if(!(self instanceof Player player)) return;
        if(player.level().isClientSide()) return;
        double multiplier = AccessoryUtil.getVisibilityMultiplier(player);
        if(multiplier != 1.0D) {
            cir.setReturnValue(cir.getReturnValue() * multiplier);
        }
    }
}
