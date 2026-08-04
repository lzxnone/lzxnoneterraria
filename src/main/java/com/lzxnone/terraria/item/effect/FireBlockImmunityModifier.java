package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface FireBlockImmunityModifier {
    //火块（火/灵魂火/岩浆块）免疫
    default boolean isFireBlockImmunity(ItemStack stack, LivingEntity entity) { return false; }
}
