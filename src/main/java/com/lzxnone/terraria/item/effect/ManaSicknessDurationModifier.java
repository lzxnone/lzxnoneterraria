package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ManaSicknessDurationModifier {
    //耐魔性持续时间比例（0.75 = 降低 25%）
    double getManaSicknessDurationMultiplier(ItemStack stack, LivingEntity entity);
}
