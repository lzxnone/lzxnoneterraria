package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ScopeZoomModifier {
    //是否提供瞄准放大能力
    boolean canZoom(ItemStack stack, LivingEntity entity);
}
