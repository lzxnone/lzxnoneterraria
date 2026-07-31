package com.lzxnone.terraria.item.accessory.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ManaCostModifier {
    double getManaCostMultiplier(ItemStack stack, LivingEntity entity);
}
