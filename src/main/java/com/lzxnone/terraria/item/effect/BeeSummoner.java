package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface BeeSummoner {
    int getBeeSummonMaxCount(ItemStack stack, LivingEntity entity);
    double getBeeSummonDamage(ItemStack stack, LivingEntity entity);
}
