package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MerchantDiscountModifier {
    //商人交易折扣比例（0.2 = 打 8 折，即价格减 20%）
    double getMerchantDiscountRatio(ItemStack stack, LivingEntity entity);
}
