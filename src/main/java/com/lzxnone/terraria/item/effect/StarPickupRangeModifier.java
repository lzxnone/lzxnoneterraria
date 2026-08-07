package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface StarPickupRangeModifier {
    //星星拾取范围（格），0 = 不扩展
    double getStarPickupRange(ItemStack stack, LivingEntity entity);
}
