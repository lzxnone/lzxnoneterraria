package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface JumpModifier {
    //跳跃高度倍率（垂直初速度）
    double getJumpHeightMultiplier(ItemStack stack, LivingEntity entity);
    //跳跃速度倍率（水平初速度）
    double getJumpSpeedMultiplier(ItemStack stack, LivingEntity entity);
}
