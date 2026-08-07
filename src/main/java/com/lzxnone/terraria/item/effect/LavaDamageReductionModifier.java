package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LavaDamageReductionModifier {
    //岩浆伤害减免倍率（0~1，如 0.5 = 减免 50%）
    double getLavaDamageReduction(ItemStack stack, LivingEntity entity);
}
