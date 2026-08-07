package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LavaImmunityModifier {
    //提供的岩浆免疫时长（刻）
    int getLavaImmunityTicks(ItemStack stack, LivingEntity entity);
}
