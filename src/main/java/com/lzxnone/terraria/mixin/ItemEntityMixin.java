package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Unique
    private static final double lzxnoneterraria$PICKUP_PROBE_RANGE = 16.0D;

    @Unique
    private static final double lzxnoneterraria$ATTRACT_SPEED = 0.25D;

    @Unique
    private boolean lzxnoneterraria$attracting = false;

    @Shadow
    private int pickupDelay;

    @WrapOperation(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;applyGravity()V")
    )
    private void lzxnoneterraria$wrapGravityWhileAttracted(ItemEntity self, Operation<Void> original) {
        if(!lzxnoneterraria$attracting) {
            original.call(self);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void lzxnoneterraria$attractToPlayer(CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object) this;
        boolean attracting = false;
        if(this.pickupDelay == 0) {
            Player player = self.level().getNearestPlayer(
                self.getX(), self.getY(), self.getZ(), lzxnoneterraria$PICKUP_PROBE_RANGE, false
            );
            if(player != null && self.getOwner() != player) {
                Item item = self.getItem().getItem();
                double range = AccessoryUtil.getEffectivePickupRange(player, item);
                if(range > 0.0D) {
                    double distanceSqr = self.distanceToSqr(player);
                    if(distanceSqr <= range * range) {
                        attracting = true;
                        Vec3 toPlayer = player.position().subtract(self.position()).normalize();
                        self.setDeltaMovement(toPlayer.scale(lzxnoneterraria$ATTRACT_SPEED));
                    }
                }
            }
        }
        lzxnoneterraria$attracting = attracting;
    }
}
