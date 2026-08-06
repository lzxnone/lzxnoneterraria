package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IceBarrierModifier {
    boolean canApplyIceBarrier(ItemStack stack, LivingEntity entity);
    double getMaxHealthRatio(ItemStack stack, LivingEntity entity);
}
