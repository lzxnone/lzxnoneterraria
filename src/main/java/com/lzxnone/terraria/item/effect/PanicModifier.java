package com.lzxnone.terraria.item.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface PanicModifier {
    //受到攻击后是否触发恐慌加速
    boolean canApplyPanic(ItemStack stack, LivingEntity entity);
}
