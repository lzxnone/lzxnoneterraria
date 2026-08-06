package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IgniteOnHitModifier {
    boolean canApplyIgnite(ItemStack stack, LivingEntity entity);
    int getIgniteTicks(ItemStack stack, LivingEntity entity);
}
