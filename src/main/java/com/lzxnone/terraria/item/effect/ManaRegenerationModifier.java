package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ManaRegenerationModifier {
    //魔力再生奖励
    double getManaRegenBonus(ItemStack stack, LivingEntity entity);
}
