package com.lzxnone.terraria.mixin;

import com.lzxnone.terraria.item.accessory.AccessoryUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void lzxnoneterraria$applyMerchantDiscount(Player player, CallbackInfo ci) {
        if(player == null) return;
        double ratio = AccessoryUtil.getMerchantDiscountRatio(player);
        if(ratio <= 0.0D) return;
        Villager self = (Villager)(Object) this;
        for(MerchantOffer offer : self.getOffers()) {
            int discount = Mth.floor(offer.getBaseCostA().getCount() * ratio);
            if(discount > 0) offer.addToSpecialPriceDiff(-discount);
        }
    }
}
