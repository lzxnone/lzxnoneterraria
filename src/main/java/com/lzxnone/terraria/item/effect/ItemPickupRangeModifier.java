package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ItemPickupRangeModifier {
    //所有掉落物的吸附范围（格），0 = 不扩展
    double getItemPickupRange(ItemStack stack, LivingEntity entity);
}
