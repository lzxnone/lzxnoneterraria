package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface MeleeKnockbackModifier {
    //近战击退加值（正数 = 增加的击退强度）
    double getMeleeKnockbackBonus(ItemStack stack, LivingEntity entity);
}
