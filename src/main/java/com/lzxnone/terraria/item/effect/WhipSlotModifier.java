package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface WhipSlotModifier {
    // 鞭子标记槽位加成
    int getWhipSlotBonus(ItemStack stack, LivingEntity entity);
}
