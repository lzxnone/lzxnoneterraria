package com.lzxnone.terraria.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lzxnone.terraria.item.ModItems;
import com.lzxnone.terraria.item.weapon.MagicWeapon;
import com.lzxnone.terraria.item.weapon.RangedWeapon;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class UseSpeedMixin {

    private boolean isCustomModWeapon(LocalPlayer player) {
        if(!player.isUsingItem()) return false;
        ItemStack usingItem = player.getUseItem();
        return usingItem.is(ModItems.ZENITH.get())
                || usingItem.is(ModItems.TRUE_COPPER_SHORTSWORD.get())
                || usingItem.is(ModItems.FIRST_FRACTAL.get())
                || usingItem.is(ModItems.TERRAGRIM.get())
                || usingItem.is(ModItems.MACE.get())
                || usingItem.getItem() instanceof MagicWeapon
                || usingItem.getItem() instanceof RangedWeapon;
    }

    @WrapOperation(
        method = "aiStep",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    )
    private boolean wrapIsUsingItemInAiStep(LocalPlayer instance, Operation<Boolean> original) {
        if(isCustomModWeapon(instance)) {
            return false;
        }
        return original.call(instance);
    }

    @WrapOperation(
        method = "canStartSprinting",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    )
    private boolean wrapIsUsingItemInCanStartSprinting(LocalPlayer instance, Operation<Boolean> original) {
        if(isCustomModWeapon(instance)) {
            return false;
        }
        return original.call(instance);
    }
}
