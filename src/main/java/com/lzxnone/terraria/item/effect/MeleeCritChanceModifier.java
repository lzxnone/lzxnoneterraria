package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MeleeCritChanceModifier {
    double getMeleeCritChance(ItemStack stack, LivingEntity entity);
}
