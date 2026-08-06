package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface VisibilityModifier {
    //可见度倍率（>1 = 更容易被怪物发现，<1 = 更难被发现；1.0 = 无效果）
    double getVisibilityMultiplier(ItemStack stack, LivingEntity entity);
}
