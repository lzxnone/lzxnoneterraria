package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface EmeraldPickupRangeModifier {
    //绿宝石掉落物吸附范围（格），0 = 不扩展
    double getEmeraldPickupRange(ItemStack stack, LivingEntity entity);
}
