package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MeleeDamageModifier {
    double getMeleeDamageMultiplier(ItemStack stack, LivingEntity entity);
}
