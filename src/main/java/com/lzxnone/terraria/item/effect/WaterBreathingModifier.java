package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface WaterBreathingModifier {
    boolean canBreatheUnderwater(ItemStack stack, LivingEntity entity);
}
