package com.lzxnone.terraria.item.accessory.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AutoManaPotionUser {
    boolean canAutoUseManaPotion(ItemStack stack, LivingEntity entity);
}
