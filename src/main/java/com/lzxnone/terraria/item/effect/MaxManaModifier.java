package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MaxManaModifier {
    //最大魔力加成
    int getMaxManaBonus(ItemStack stack, LivingEntity entity);
}
