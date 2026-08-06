package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface GuardianShieldModifier {
    boolean applyGuardianShield(ItemStack stack, LivingEntity entity);
    double getGuardianRange(ItemStack stack, LivingEntity entity);
    double getMinHealthRatio(ItemStack stack, LivingEntity entity);
}
